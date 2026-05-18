package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.dto.request.RegistrazioneRequest;
import com.example.sitoartepsaw.dto.response.UtenteResponse;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.mapper.UtenteMapper;
import com.example.sitoartepsaw.repository.UtenteRepository;
import com.example.sitoartepsaw.support.exceptions.ConflictException;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Transactional
    public UtenteResponse registraUtente(RegistrazioneRequest request) {

        // Crea utente su Keycloak via REST API
        creaUtenteKeycloak(request);

        // Salva nel DB senza password — la gestisce Keycloak
        Utente utente = utenteMapper.toEntity(request);
        Utente salvato = utenteRepository.save(utente);

        return utenteMapper.toResponse(salvato);
    }


    @Transactional(readOnly = true)
    public UtenteResponse getProfiloUtente(String email) {
        Utente utente = utenteRepository
                .findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente con email " + email + " non trovato"
                ));
        return utenteMapper.toResponse(utente);
    }

    @Transactional(readOnly = true)
    public UtenteResponse getUtenteById(Integer id) {
        Utente utente = utenteRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente con id " + id + " non trovato"
                ));
        return utenteMapper.toResponse(utente);
    }

    // Ottiene un token admin fresco ad ogni chiamata
    // Il token admin scade ogni 5 minuti quindi non si può salvare
    private String getAdminToken() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(keycloakServerUrl
                            + "/realms/master/protocol/openid-connect/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "client_id=admin-cli" +
                            "&username=" + adminUsername +
                            "&password=" + adminPassword +
                            "&grant_type=password"
                    ))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body()
                    .split("\"access_token\":\"")[1]
                    .split("\"")[0];

        } catch (Exception e) {
            throw new RuntimeException("Errore ottenimento admin token Keycloak: "
                    + e.getMessage());
        }
    }

    // Crea l'utente su Keycloak tramite REST API
    private void creaUtenteKeycloak(RegistrazioneRequest request) {
        try {
            String body = String.format("""
            {
                "username": "%s",
                "email": "%s",
                "firstName": "%s",
                "lastName": "%s",
                "enabled": true,
                "credentials": [{
                    "type": "password",
                    "value": "%s",
                    "temporary": false
                }]
            }
            """,
                    request.getEmail(),
                    request.getEmail(),
                    request.getNome(),
                    request.getCognome(),
                    request.getPassword()
            );

            HttpClient client = HttpClient.newHttpClient();
            String adminToken = getAdminToken();

            // Crea l'utente
            HttpRequest createRequest = HttpRequest.newBuilder()
                    .uri(URI.create(keycloakServerUrl + "/admin/realms/" + realm + "/users"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + adminToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> createResponse = client.send(
                    createRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (createResponse.statusCode() != 201) {
                throw new RuntimeException("Errore creazione utente su Keycloak: "
                        + createResponse.body());
            }

            // Estrae l'id dell'utente appena creato dall'header Location
            String location = createResponse.headers()
                    .firstValue("Location")
                    .orElseThrow(() -> new RuntimeException("Location header non trovato"));
            String userId = location.substring(location.lastIndexOf("/") + 1);

            // Recupera l'id del ruolo USER da Keycloak
            HttpRequest getRoleRequest = HttpRequest.newBuilder()
                    .uri(URI.create(keycloakServerUrl + "/admin/realms/" + realm + "/roles/USER"))
                    .header("Authorization", "Bearer " + adminToken)
                    .GET()
                    .build();

            HttpResponse<String> getRoleResponse = client.send(
                    getRoleRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            // Assegna il ruolo USER all'utente
            HttpRequest assignRoleRequest = HttpRequest.newBuilder()
                    .uri(URI.create(keycloakServerUrl
                            + "/admin/realms/" + realm
                            + "/users/" + userId
                            + "/role-mappings/realm"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + adminToken)
                    .POST(HttpRequest.BodyPublishers.ofString("[" + getRoleResponse.body() + "]"))
                    .build();

            HttpResponse<String> assignResponse = client.send(
                    assignRoleRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (assignResponse.statusCode() != 204) {
                throw new RuntimeException("Errore assegnazione ruolo USER: "
                        + assignResponse.body());
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore comunicazione con Keycloak: "
                    + e.getMessage());
        }
    }
}
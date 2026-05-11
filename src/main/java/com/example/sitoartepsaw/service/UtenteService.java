package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.dto.request.RegistrazioneRequest;
import com.example.sitoartepsaw.dto.response.UtenteResponse;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.mapper.UtenteMapper;
import com.example.sitoartepsaw.repository.UtenteRepository;
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

    public boolean existsByEmail(String email) {
        return utenteRepository.existsByEmail(email);
    }

    @Transactional
    public UtenteResponse registraUtente(RegistrazioneRequest request) {

        if (utenteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email già in uso: " + request.getEmail());
        }

        // Crea utente su Keycloak via REST API
        creaUtenteKeycloak(request);

        // Salva nel tuo DB (senza password)
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
                .orElseThrow(() -> new RuntimeException("Utente non trovato dalla mail"));
        return utenteMapper.toResponse(utente);
    }

    @Transactional(readOnly = true)
    public UtenteResponse getUtenteById(Integer id) {
        Utente utente = utenteRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Nessun utente associato all'ID"));
        return utenteMapper.toResponse(utente);
    }

    // ======================== KEYCLOAK ========================

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
                                    "&username=" + adminUsername+
                                    "&password=" + adminPassword+
                                    "&grant_type=password"
                    ))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // Estrae access_token dalla risposta JSON
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
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(keycloakServerUrl
                            + "/admin/realms/" + realm + "/users"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getAdminToken())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 201) {
                throw new RuntimeException("Errore creazione utente su Keycloak: "
                        + response.body());
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore comunicazione con Keycloak: "
                    + e.getMessage());
        }
    }
}
package com.example.sitoartepsaw.support;

import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.repository.UtenteRepository;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("proxyVerificatoService")
@RequiredArgsConstructor
public class ProxyVerificatoService {

    // Controlla se l'utente ha il ruolo USER_VERIFICATO nel token Keycloak
    // senza fare query al DB
    public boolean isVerificato(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER_VERIFICATO"));
    }
}

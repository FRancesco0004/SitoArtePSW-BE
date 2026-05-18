package com.example.sitoartepsaw.support;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {

    // Estrae l'email dell'utente loggato direttamente dal token Keycloak
    // senza dover passare il JWT come parametro nei metodi
    public static String getEmail() {
        JwtAuthenticationToken authenticationToken =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return (String) jwt.getClaims().get("email");
    }
}
package com.example.sitoartepsaw.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.keycloak")
public record KeycloakProperties(
        String serverUrl,
        String realm,
        String adminUsername,
        String adminPassword
) {}

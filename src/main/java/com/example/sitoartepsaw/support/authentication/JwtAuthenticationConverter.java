package com.example.sitoartepsaw.support.authentication;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // Converter standard di Spring per estrarre i ruoli dal realm_access del token Keycloak
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    // Converte il token JWT di Keycloak in un oggetto Spring Security
    // combinando i ruoli del realm con i ruoli delle risorse
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipleClaimName(jwt));
    }

    // Estrae i ruoli dalla sezione resource_access del token
    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) return Set.of();

        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get("art-platform-client");

        if (resource == null) return Set.of();

        List<String> resourceRoles = (List<String>) resource.get("roles");

        if (resourceRoles == null) return Set.of();

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    // Usa l'email come identificatore principale dell'utente
    private String getPrincipleClaimName(Jwt jwt) {
        return jwt.getClaim("email");
    }
}
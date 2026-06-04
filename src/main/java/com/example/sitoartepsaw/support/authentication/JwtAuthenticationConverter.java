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

    // Converter standard di Spring per estrarre gli scope dal token JWT
    // Esempio: SCOPE_email, SCOPE_profile
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    // Converte il token JWT di Keycloak in un oggetto Spring Security,
    // combinando scope standard, ruoli di realm e ruoli delle risorse.
    // In questo modo @PreAuthorize può controllare correttamente i ruoli dell'utente.
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> scopeAuthorities =
                jwtGrantedAuthoritiesConverter.convert(jwt);

        Collection<GrantedAuthority> realmAuthorities =
                extractRealmRoles(jwt);

        Collection<GrantedAuthority> resourceAuthorities =
                extractResourceRoles(jwt);

        Collection<GrantedAuthority> authorities = Stream
                .of(scopeAuthorities, realmAuthorities, resourceAuthorities)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipleClaimName(jwt));
    }

    // Estrae i ruoli dalla sezione realm_access del token Keycloak.
    // Esempio: USER, USER_VERIFICATO.
    // Ogni ruolo viene trasformato in ROLE_<ruolo> perché hasRole(...) di Spring
    // cerca authorities con prefisso ROLE_.
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return Set.of();
        }

        Object rolesObject = realmAccess.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return Set.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    // Estrae eventuali ruoli dalla sezione resource_access del token Keycloak,
    // relativi al client dell'applicazione.
    // Anche questi vengono trasformati in ROLE_<ruolo>.
    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return Set.of();
        }

        Object resourceObject = resourceAccess.get("art-platform-client");

        if (!(resourceObject instanceof Map<?, ?> resource)) {
            return Set.of();
        }

        Object rolesObject = resource.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return Set.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String email = jwt.getClaim("email");

        if (email != null && !email.isBlank()) {
            return email;
        }

        return jwt.getSubject();
    }
}
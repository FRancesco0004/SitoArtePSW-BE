package com.example.sitoartepsaw.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF disabilitato: l'autenticazione avviene tramite JWT nell'header,
                // non con cookie di sessione, quindi l'attacco CSRF non è applicabile
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Registrazione pubblica
                        .requestMatchers("/api/utenti/registra").permitAll()
                        // Tutto il resto richiede un JWT valido di Keycloak
                        .anyRequest().authenticated()
                )
                // Keycloak valida il JWT ad ogni richiesta tramite la chiave pubblica del realm
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                // Nessuna sessione HTTP — ogni richiesta porta il suo JWT.
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(STATELESS)
                );

        return http.build();
    }

    // Permette le richieste dal frontend al backend
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS"); // necessario per il preflight CORS
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
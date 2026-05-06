package com.example.sitoartepsaw.support.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// Questa classe controlla il token a ogni richiesta HTTP e viene chiamato automaticamente
// da Spring Security

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Prende l'header Authorization dalla richiesta
        String authHeader = request.getHeader("Authorization");

        // Se non c'è il token o non inizia con "Bearer ", lascia passare
        // Spring Security deciderà se la richiesta è permessa o no
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String email = jwtTokenProvider.extractEmail(token);

        // Carica l'utente dal DB solo se non è già autenticato
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Crea l'oggetto di autenticazione e lo mette nel contesto
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Dice a Spring Security che l'utente è autenticato
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Continua con la richiesta
        filterChain.doFilter(request, response);
    }
}

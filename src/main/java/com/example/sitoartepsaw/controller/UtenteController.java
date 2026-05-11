package com.example.sitoartepsaw.controller;

import com.example.sitoartepsaw.dto.request.RegistrazioneRequest;
import com.example.sitoartepsaw.dto.response.UtenteResponse;
import com.example.sitoartepsaw.service.UtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    @PostMapping("/registra")
    public ResponseEntity<UtenteResponse> registra(
            @Valid @RequestBody RegistrazioneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(utenteService.registraUtente(request));
    }

    @GetMapping("/profilo")
    public ResponseEntity<UtenteResponse> getProfilo(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                utenteService.getProfiloUtente(jwt.getClaimAsString("email"))
        );
    }
}

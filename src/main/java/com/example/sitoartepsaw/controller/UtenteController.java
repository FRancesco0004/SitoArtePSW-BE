package com.example.sitoartepsaw.controller;

import com.example.sitoartepsaw.dto.request.LoginRequest;
import com.example.sitoartepsaw.dto.request.RegistrazioneRequest;
import com.example.sitoartepsaw.dto.response.UtenteResponse;
import com.example.sitoartepsaw.service.UtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    // La notazione @Valid attiva le validazioni specificate nel RegistrazioneRequest nei DTO
    // La notazione @RequestBody mi serve per il JSON dalla richiesta HTTP
    @PostMapping("/registra")
    public ResponseEntity<UtenteResponse> registra(@Valid @RequestBody RegistrazioneRequest request) {
        UtenteResponse response = utenteService.registra(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // La notazione @Valid attiva le validazioni specificate nel LoginRequest nei DTO
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = utenteService.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/profilo")
    public ResponseEntity<UtenteResponse> getProfilo(@AuthenticationPrincipal UserDetails userDetails) {

        UtenteResponse response = utenteService.getProfilo(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}

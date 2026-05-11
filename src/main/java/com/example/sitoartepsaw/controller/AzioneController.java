package com.example.sitoartepsaw.controller;

import com.example.sitoartepsaw.dto.request.AcquistoRequest;
import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.repository.UtenteRepository;
import com.example.sitoartepsaw.service.AzioneService;
import com.example.sitoartepsaw.service.facade.AcquistoFacade;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/azioni")
@RequiredArgsConstructor
public class AzioneController {

    private final AzioneService azioneService;
    private final AcquistoFacade acquistoFacade;
    private final UtenteRepository utenteRepository;

    @PostMapping("/compra/{oggettoId}")
    public ResponseEntity<AzioneResponse> compra(
            @PathVariable Integer oggettoId,
            @Valid @RequestBody AcquistoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // Carica l'utente dal DB usando l'email del token
        String email = jwt.getClaimAsString("email");
        Utente utente = utenteRepository.findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        AzioneResponse response = acquistoFacade.compra(oggettoId, request, utente);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lo implementiamo dopo, quando creiamo la logica di vendita nella facade
    /*
    @PostMapping("/vendi/{oggettoId}")
    @PreAuthorize("@proxyVerificatoService.isVerificato(#utente.id)")
    public ResponseEntity<AzioneResponse> vendi(
            @PathVariable Integer oggettoId,
            @Valid @RequestBody AcquistoRequest request,
            @AuthenticationPrincipal Utente utente
    ) {
        AzioneResponse response = acquistoFacade.vendi(oggettoId, request, utente);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    */

    @GetMapping("/storico")
    public ResponseEntity<List<AzioneResponse>> getStorico(
            @AuthenticationPrincipal Utente utente
    ) {
        List<AzioneResponse> storico = azioneService.getStorico(utente.getId());
        return ResponseEntity.ok(storico);
    }

    @GetMapping("/storico/{id}")
    public ResponseEntity<AzioneResponse> getAzione(
            @PathVariable Integer id,
            @AuthenticationPrincipal Utente utente
    ) {
        AzioneResponse response = azioneService.getAzione(id, utente.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/annulla/{id}")
    public ResponseEntity<AzioneResponse> annulla(
            @PathVariable Integer id,
            @AuthenticationPrincipal Utente utente
    ) {
        AzioneResponse response = azioneService.annulla(id, utente.getId());
        return ResponseEntity.ok(response);
    }
}
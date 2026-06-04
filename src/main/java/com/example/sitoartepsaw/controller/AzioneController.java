package com.example.sitoartepsaw.controller;

import com.example.sitoartepsaw.dto.request.AcquistoRequest;
import com.example.sitoartepsaw.dto.request.VenditaRequest;
import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.service.AzioneService;
import com.example.sitoartepsaw.service.facade.VenditaFacade;
import com.example.sitoartepsaw.service.facade.AcquistoFacade;
import com.example.sitoartepsaw.support.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/azioni")
@RequiredArgsConstructor
public class AzioneController {

    private final AzioneService azioneService;
    private final AcquistoFacade acquistoFacade;
    private final VenditaFacade venditaFacade;

    @PostMapping("/compra/{oggettoId}")
    public ResponseEntity<AzioneResponse> compra(
            @PathVariable Integer oggettoId,
            @Valid @RequestBody AcquistoRequest request
    ) {
        String email = Utils.getEmail();

        AzioneResponse response = acquistoFacade.compra(oggettoId, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/debug-authorities")
    public ResponseEntity<List<String>> debugAuthorities(
            org.springframework.security.core.Authentication authentication
    ) {
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        System.out.println("AUTHORITIES BACKEND:");
        authorities.forEach(System.out::println);

        return ResponseEntity.ok(authorities);
    }

    @PreAuthorize("hasRole('USER_VERIFICATO')")
    @PostMapping("/vendi")
    public ResponseEntity<AzioneResponse> vendi(
            @Valid @RequestBody VenditaRequest request
    ) {
        String email = Utils.getEmail();

        AzioneResponse response = venditaFacade.vendi(request, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/storico")
    public ResponseEntity<List<AzioneResponse>> getStorico() {
        String email = Utils.getEmail();

        List<AzioneResponse> storico = azioneService.getStorico(email);

        return ResponseEntity.ok(storico);
    }

    @GetMapping("/storico/{id}")
    public ResponseEntity<AzioneResponse> getAzione(
            @PathVariable Integer id
    ) {
        String email = Utils.getEmail();

        AzioneResponse response = azioneService.getAzione(id, email);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/annulla/{id}")
    public ResponseEntity<AzioneResponse> annulla(
            @PathVariable Integer id
    ) {
        String email = Utils.getEmail();

        AzioneResponse response = azioneService.annulla(id, email);

        return ResponseEntity.ok(response);
    }
}
package com.example.sitoartepsaw.controller;

import com.example.sitoartepsaw.dto.request.AcquistoRequest;
import com.example.sitoartepsaw.dto.request.VenditaRequest;
import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.service.azione.AzioneService;
import com.example.sitoartepsaw.service.compravendita.facade.VenditaFacade;
import com.example.sitoartepsaw.service.compravendita.facade.AcquistoFacade;
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

    @PostMapping("/vendi")
    @PreAuthorize("hasRole('USER_VERIFICATO')")
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
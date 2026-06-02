package com.example.sitoartepsaw.controller;

import com.example.sitoartepsaw.dto.response.OggettoAnteprimaResponse;
import com.example.sitoartepsaw.dto.response.OggettoDettaglioResponse;
import com.example.sitoartepsaw.dto.response.OggettoGiocoResponse;
import com.example.sitoartepsaw.service.OggettoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oggetti")
@RequiredArgsConstructor
public class OggettoController {

    private final OggettoService oggettoService;

    @GetMapping("/gioco-prezzo")
    public ResponseEntity<List<OggettoGiocoResponse>> getCinqueOpereCasuali() {
        return ResponseEntity.ok(oggettoService.getCinqueOpereCasuali());
    }

    @GetMapping("/catalogo")
    public ResponseEntity<Page<OggettoAnteprimaResponse>> getCatalogo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(oggettoService.getCatalogo(pageable));
    }

    @GetMapping("/id")
    public ResponseEntity<OggettoDettaglioResponse> getDettaglioOggetto(@RequestParam Integer id) {
        return ResponseEntity.ok(oggettoService.getDettaglioOggetto(id));
    }
}
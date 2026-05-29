package com.example.sitoartepsaw.controller;

import com.example.sitoartepsaw.dto.response.OggettoGiocoResponse;
import com.example.sitoartepsaw.service.GiocoPrezzoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gioco-prezzo")
@RequiredArgsConstructor
public class GiocoPrezzoController {

    private final GiocoPrezzoService giocoPrezzoService;

    @GetMapping("/opere")
    public ResponseEntity<List<OggettoGiocoResponse>> getCinqueOpereCasuali() {
        return ResponseEntity.ok(giocoPrezzoService.getCinqueOpereCasuali());
    }
}
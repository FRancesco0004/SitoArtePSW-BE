package com.example.sitoartepsaw.service.oggetto;

import com.example.sitoartepsaw.dto.response.OggettoAnteprimaResponse;
import com.example.sitoartepsaw.dto.response.OggettoDettaglioResponse;
import com.example.sitoartepsaw.dto.response.OggettoGiocoResponse;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.mapper.OggettoMapper;
import com.example.sitoartepsaw.repository.OggettoRepository;
import com.example.sitoartepsaw.support.exceptions.BadRequestException;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OggettoService {

    private final OggettoMapper oggettoMapper;
    private final OggettoRepository oggettoRepository;

    @Transactional(readOnly = true)
    public List<OggettoGiocoResponse> getCinqueOpereCasuali() {
        List<Oggetto> opere = oggettoRepository.findCinqueDisponibiliRandom();
        if (opere.size() < 5) {
            throw new BadRequestException("Non ci sono abbastanza opere disponibili");
        }
        return opere.stream()
                .map(oggettoMapper::toGiocoResponse)  // ← usa il mapper
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<OggettoAnteprimaResponse> getCatalogo(Pageable pageable) {
        return oggettoRepository
                .findByStato(StatoOggetto.DISPONIBILE, pageable)
                .map(oggettoMapper::toAnteprimaResponse);  // ← usa il mapper
    }

    @Transactional(readOnly = true)
    public OggettoDettaglioResponse getDettaglioOggetto(Integer id) {
        Oggetto oggetto = oggettoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Oggetto con id " + id + " non trovato"));
        return oggettoMapper.toDettaglioResponse(oggetto);
    }
}
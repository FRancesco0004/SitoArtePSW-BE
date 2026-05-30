package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.dto.response.OggettoGiocoResponse;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.repository.OggettoRepository;
import com.example.sitoartepsaw.support.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GiocoPrezzoService {

    private final OggettoRepository oggettoRepository;

    @Transactional(readOnly = true)
    public List<OggettoGiocoResponse> getCinqueOpereCasuali() {
        List<Oggetto> opere = oggettoRepository.findCinqueDisponibiliRandom();

        if (opere.size() < 5) {
            throw new BadRequestException("Non ci sono abbastanza opere disponibili");
        }

        return opere.stream()
                .map(this::toOperaGiocoResponse)
                .toList();
    }

    private OggettoGiocoResponse toOperaGiocoResponse(Oggetto oggetto) {
        return OggettoGiocoResponse.builder()
                .id(oggetto.getId())
                .titolo(oggetto.getTitolo())
                .descrizione(oggetto.getDescrizione())
                .anno(oggetto.getAnno())
                .grandezza(oggetto.getGrandezza())
                .linkImmagine(oggetto.getLinkImmagine())
                .tipoOpera(oggetto.getTipoOpera())
                .peso(oggetto.getPeso())
                .nomeAutore(oggetto.getAutore() != null ? oggetto.getAutore().getNome() : null)
                .cognomeAutore(oggetto.getAutore() != null ? oggetto.getAutore().getCognome() : null)
                .build();
    }
}
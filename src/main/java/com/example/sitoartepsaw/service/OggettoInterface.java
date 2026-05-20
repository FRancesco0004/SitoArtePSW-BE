package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.dto.request.VenditaRequest;
import com.example.sitoartepsaw.entity.Autore;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.enums.TipoOpera;

public interface OggettoInterface {

    TipoOpera getTipoOpera();

    default Oggetto crea(VenditaRequest request, Autore autore) {
        return Oggetto.builder()
                .titolo(request.getTitolo())
                .descrizione(request.getDescrizione())
                .anno(request.getAnno())
                .costo(request.getCosto())
                .grandezza(request.getGrandezza())
                .linkImmagine(request.getLinkImmagine())
                .tipoOpera(getTipoOpera())
                .peso(request.getPeso())
                .stato(StatoOggetto.DISPONIBILE)
                .autore(autore)
                .build();
    }
}
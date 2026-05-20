package com.example.sitoartepsaw.service.oggettoFactory;

import com.example.sitoartepsaw.dto.request.VenditaRequest;
import com.example.sitoartepsaw.entity.Autore;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.enums.TipoOpera;
import com.example.sitoartepsaw.service.OggettoInterface;
import com.example.sitoartepsaw.support.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OggettoFactory {

    private final Map<TipoOpera, OggettoInterface> tipiOggetto;

    public OggettoFactory(List<OggettoInterface> oggetti) {
        this.tipiOggetto = new HashMap<>();

        for (OggettoInterface oggetto : oggetti) {
            this.tipiOggetto.put(oggetto.getTipoOpera(), oggetto);
        }
    }

    public Oggetto crea(VenditaRequest request, Autore autore) {
        TipoOpera tipoOpera = request.getTipoOpera();

        if (tipoOpera == null || !tipiOggetto.containsKey(tipoOpera)) {
            throw new BadRequestException("Tipo opera non valido o non supportato");
        }

        return tipiOggetto.get(tipoOpera).crea(request, autore);
    }
}
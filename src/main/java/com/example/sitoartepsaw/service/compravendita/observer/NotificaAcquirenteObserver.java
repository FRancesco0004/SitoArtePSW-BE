package com.example.sitoartepsaw.service.compravendita.observer;

import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificaAcquirenteObserver implements AcquistoObserver {

    @Override
    public void aggiorna(Oggetto oggetto, Utente acquirente) {
        log.info(
                "EMAIL ACQUIRENTE | Ciao {}, il tuo acquisto dell'oggetto '{}' è stato completato con successo. Importo: {}",
                acquirente.getEmail(),
                oggetto.getTitolo(),
                oggetto.getCosto()
        );
    }
}
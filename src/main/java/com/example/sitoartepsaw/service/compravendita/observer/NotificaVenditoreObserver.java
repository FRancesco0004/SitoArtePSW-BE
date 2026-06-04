package com.example.sitoartepsaw.service.compravendita.observer;

import com.example.sitoartepsaw.entity.Azione;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.enums.TipoAzione;
import com.example.sitoartepsaw.repository.AzioneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificaVenditoreObserver implements AcquistoObserver {

    private final AzioneRepository azioneRepository;

    @Override
    public void aggiorna(Oggetto oggetto, Utente acquirente) {
        Azione azioneVendita = azioneRepository
                .findFirstByOggettoIdAndTipoAzioneAndAnnullataFalseOrderByDataDesc(
                        oggetto.getId(),
                        TipoAzione.VENDE
                )
                .orElse(null);

        if (azioneVendita == null) {
            log.warn(
                    "EMAIL VENDITORE NON INVIATA | Nessuna azione di vendita trovata per l'oggetto con id {}",
                    oggetto.getId()
            );
            return;
        }

        Utente venditore = azioneVendita.getUtente();

        log.info(
                "EMAIL VENDITORE | Ciao {}, la tua opera '{}' è stata acquistata da {}. Importo: {}",
                venditore.getEmail(),
                oggetto.getTitolo(),
                acquirente.getEmail(),
                oggetto.getCosto()
        );
    }
}
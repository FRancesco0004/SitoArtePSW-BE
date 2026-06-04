package com.example.sitoartepsaw.service.compravendita.observer;

import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;

public interface AcquistoObserver {

    void aggiorna(Oggetto oggetto, Utente acquirente);
}

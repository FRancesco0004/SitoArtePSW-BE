package com.example.sitoartepsaw.service.observer;

import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;

public interface AcquistoObserver {

    void aggiorna(Oggetto oggetto, Utente acquirente);
}

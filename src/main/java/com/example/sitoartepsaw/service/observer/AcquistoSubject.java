package com.example.sitoartepsaw.service.observer;

import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AcquistoSubject {

    private final List<AcquistoObserver> observers;

    public AcquistoSubject(List<AcquistoObserver> observers) {
        this.observers = observers;
    }

    public void notificaAcquisto(Oggetto oggetto, Utente acquirente) {
        observers.forEach(observer -> observer.aggiorna(oggetto, acquirente));
    }
}
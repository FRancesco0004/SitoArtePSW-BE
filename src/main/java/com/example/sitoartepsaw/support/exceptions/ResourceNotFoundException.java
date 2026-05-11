package com.example.sitoartepsaw.support.exceptions;

// Da usare per:
// - Utente non trovato
// - Azione non trovata
// - Oggetto non trovato
// - Autore non trovato

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

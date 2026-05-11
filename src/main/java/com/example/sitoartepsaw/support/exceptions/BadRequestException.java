package com.example.sitoartepsaw.support.exceptions;

// Da usare quando vi sono richieste logicamente sbagliate:
// - Prezzo negativo
// - Operazione non consentita per i dati inseriti

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
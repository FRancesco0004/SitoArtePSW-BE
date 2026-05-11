package com.example.sitoartepsaw.support.exceptions;
//Da usare per:
// - email già in uso
// - azione già annullata
// - oggetto già venduto


public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

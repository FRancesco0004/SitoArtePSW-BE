package com.example.sitoartepsaw.service.payment;

import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.enums.MetodoPagamento;
import com.example.sitoartepsaw.support.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j // Lombok crea il logger automaticamente
public abstract class PagamentoTemplate {

    // Metodo da richiamare poi nella Facade
    public final String esegui(BigDecimal importo, Utente utente) {

        validaImporto(importo);
        String transactionId = chiamaApi(importo, utente); // metodo da implementare in ogni classe
        // dal momento che fingeremo i pagamenti, usiamo questo metodo per il DEBUG
        registraTransazione(transactionId, importo, utente);
        return transactionId;
    }

    private void validaImporto(BigDecimal importo) {
        if (importo == null || importo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Importo non valido");
        }
    }

    public abstract MetodoPagamento getMetodoPagamento();

    protected abstract String chiamaApi(BigDecimal importo, Utente utente);

    private void registraTransazione(String transactionId,
                                     BigDecimal importo,
                                     Utente utente) {
        log.info("Transazione registrata: {} | Importo: {} | Utente: {}",
                transactionId, importo, utente.getEmail());
    }
}

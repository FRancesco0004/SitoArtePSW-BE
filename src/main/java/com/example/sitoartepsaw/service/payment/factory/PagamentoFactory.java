package com.example.sitoartepsaw.service.payment.factory;

import com.example.sitoartepsaw.enums.MetodoPagamento;
import com.example.sitoartepsaw.service.payment.*;
import com.example.sitoartepsaw.support.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class PagamentoFactory {

    private HashMap<MetodoPagamento, PagamentoTemplate> tipiPagamento = new HashMap<>();

    public PagamentoFactory(List<PagamentoTemplate> pagamenti) {
        this.tipiPagamento = new HashMap<>();

        for (PagamentoTemplate pagamento : pagamenti) {
            this.tipiPagamento.put(pagamento.getMetodoPagamento(), pagamento);
        }
    }

    public PagamentoTemplate get(MetodoPagamento metodo) {
        if (metodo == null || !tipiPagamento.containsKey(metodo)) {
            throw new BadRequestException("Metodo di pagamento non valido o non supportato");
        }

        return this.tipiPagamento.get(metodo);
    }
}
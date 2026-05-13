package com.example.sitoartepsaw.service.payment.factory;

import com.example.sitoartepsaw.enums.MetodoPagamento;
import com.example.sitoartepsaw.service.payment.*;
import com.example.sitoartepsaw.support.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoFactory {

    private final PaypalPagamento paypalPagamento;
    private final BonificoPagamento bonificoPagamento;
    private final P2PPagamento p2pPagamento;
    private final RevolutPagamento revolutPagamento;

    public PagamentoTemplate get(MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new BadRequestException("Metodo di pagamento obbligatorio");
        }

        return switch (metodoPagamento) {
            case PAYPAL -> paypalPagamento;
            case BONIFICO -> bonificoPagamento;
            case P2P -> p2pPagamento;
            case REVOLUT -> revolutPagamento;
        };
    }
}
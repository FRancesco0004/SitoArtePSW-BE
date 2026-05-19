package com.example.sitoartepsaw.service.payment;


import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.enums.MetodoPagamento;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class RevolutPagamento extends PagamentoTemplate {

    @Override
    protected String chiamaApi(BigDecimal importo, Utente utente) {
        System.out.println("Hai pagato "+importo+" € con Revolut");
        return "REVOLUT-" + UUID.randomUUID();
    }

    public MetodoPagamento getMetodoPagamento(){
        return MetodoPagamento.REVOLUT;
    }
}

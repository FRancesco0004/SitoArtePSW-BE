package com.example.sitoartepsaw.service.payment;

import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.enums.MetodoPagamento;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class P2PPagamento extends PagamentoTemplate {

    @Override
    protected String chiamaApi(BigDecimal importo, Utente utente) {
        System.out.println("Hai pagato "+importo+" € con P2P");
        return "P2P-" + UUID.randomUUID();
    }

    public MetodoPagamento getMetodoPagamento(){
        return MetodoPagamento.P2P;
    }
}

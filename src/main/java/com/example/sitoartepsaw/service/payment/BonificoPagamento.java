package com.example.sitoartepsaw.service.payment;

import com.example.sitoartepsaw.entity.Utente;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class BonificoPagamento extends PagamentoTemplate {

    @Override
    protected String chiamaApi(BigDecimal importo, Utente utente) {
        System.out.println("Hai pagato "+importo+" € con un Bonifico");
        return "BONIFICO-" + UUID.randomUUID();
    }
}

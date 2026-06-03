package com.example.sitoartepsaw.service.facade;

import com.example.sitoartepsaw.dto.request.AcquistoRequest;
import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.repository.OggettoRepository;
import com.example.sitoartepsaw.repository.UtenteRepository;
import com.example.sitoartepsaw.service.AzioneService;
import com.example.sitoartepsaw.service.observer.AcquistoSubject;
import com.example.sitoartepsaw.service.payment.paymentFactory.PagamentoFactory;
import com.example.sitoartepsaw.service.payment.PagamentoTemplate;
import com.example.sitoartepsaw.support.exceptions.ConflictException;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class AcquistoFacade {

    private final OggettoRepository oggettoRepository;
    private final AzioneService azioneService;
    private final PagamentoFactory pagamentoFactory;
    private final AcquistoSubject acquistoSubject;
    private final UtenteRepository utenteRepository;

    @Transactional
    public AzioneResponse compra(
            Integer oggettoId,
            AcquistoRequest request,
            String email
    ) {
        Utente utente = utenteRepository.findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        Oggetto oggetto = oggettoRepository.findById(oggettoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Oggetto con id " + oggettoId + " non trovato"
                ));

        if (!oggetto.getStato().equals(StatoOggetto.DISPONIBILE)) {
            throw new ConflictException(
                    "L'oggetto con id " + oggettoId + " non è disponibile per l'acquisto"
            );
        }

        boolean scontoApplicato = isScontoApplicato(oggetto, request);
        BigDecimal prezzoFinale = calcolaPrezzoFinale(oggetto, request, scontoApplicato);
        PagamentoTemplate pagamento = pagamentoFactory.get(request.getMetodoPagamento());

        pagamento.esegui(prezzoFinale, utente);

        oggetto.setStato(StatoOggetto.VENDUTO);
        oggettoRepository.save(oggetto);

        AzioneResponse response = azioneService.creaAzioneAcquisto(
                oggetto,
                request,
                utente,
                prezzoFinale,
                scontoApplicato

        );

        response.setScontoApplicato(scontoApplicato);

        acquistoSubject.notificaAcquisto(oggetto, utente);

        return response;
    }

    private boolean isScontoApplicato(Oggetto oggetto, AcquistoRequest request) {
        if (request.getPrezzoStimato() == null) {
            return false;
        }

        BigDecimal prezzoReale = oggetto.getCosto();
        BigDecimal prezzoStimato = request.getPrezzoStimato();

        BigDecimal differenza = prezzoReale.subtract(prezzoStimato).abs();
        BigDecimal rangeAccettato = prezzoReale.multiply(new BigDecimal("0.05"));

        return differenza.compareTo(rangeAccettato) <= 0;
    }

    private BigDecimal calcolaPrezzoFinale(
            Oggetto oggetto,
            AcquistoRequest request,
            boolean scontoApplicato
    ) {
        BigDecimal prezzoReale = oggetto.getCosto();

        if (request.getPrezzoStimato() == null || !scontoApplicato) {
            return prezzoReale;
        }

        BigDecimal sconto = new BigDecimal("0.05");
        BigDecimal importoSconto = prezzoReale.multiply(sconto);

        return prezzoReale
                .subtract(importoSconto)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
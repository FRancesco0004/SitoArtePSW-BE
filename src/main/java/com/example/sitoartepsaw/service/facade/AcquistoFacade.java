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

        BigDecimal prezzoFinale = calcolaPrezzoFinale(oggetto, request);

        PagamentoTemplate pagamento = pagamentoFactory.get(request.getMetodoPagamento());

        pagamento.esegui(prezzoFinale, utente);

        oggetto.setStato(StatoOggetto.VENDUTO);

        oggettoRepository.save(oggetto);

        //Metodo aggiornato, prende il prezzo in caso di sconto ora
        AzioneResponse response = azioneService.creaAzioneAcquisto(
                oggetto,
                request,
                utente,
                prezzoFinale
        );

        acquistoSubject.notificaAcquisto(oggetto, utente);

        return response;
    }

    private BigDecimal calcolaPrezzoFinale(Oggetto oggetto, AcquistoRequest request) {
        BigDecimal prezzoReale = oggetto.getCosto();

        //Se non si gioca
        if (request.getPrezzoStimato() == null) {
            return prezzoReale;
        }

        BigDecimal prezzoStimato = request.getPrezzoStimato();

        //scarto dal prezzo reale
        BigDecimal differenza = prezzoReale.subtract(prezzoStimato).abs();

        BigDecimal rangeAccettato = prezzoReale.multiply(new BigDecimal("0.05"));

        //se lo scarto è <= 0 la guess è corretta
        boolean guessCorretta = differenza.compareTo(rangeAccettato) <= 0;

        if (!guessCorretta) {return prezzoReale;
        }

        //Da qui controlliamo di quanto scontare in caso di vincita
        final BigDecimal scontoApplicato = new BigDecimal("0.05");
        BigDecimal importoSconto = prezzoReale.multiply(scontoApplicato);

        //il prezzo finale è il prezzo totale - lo sconto
        // (teniamo solo due cifre dopo la virgola, arrotondando in eccesso)
        return prezzoReale.subtract(importoSconto).setScale(2, RoundingMode.HALF_UP);
    }

}
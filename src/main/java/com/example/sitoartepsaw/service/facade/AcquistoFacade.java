package com.example.sitoartepsaw.service.facade;

import com.example.sitoartepsaw.dto.request.AcquistoRequest;
import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.repository.OggettoRepository;
import com.example.sitoartepsaw.service.AzioneService;
import com.example.sitoartepsaw.service.observer.AcquistoSubject;
import com.example.sitoartepsaw.service.payment.factory.PagamentoFactory;
import com.example.sitoartepsaw.service.payment.PagamentoTemplate;
import com.example.sitoartepsaw.support.exceptions.ConflictException;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AcquistoFacade {

    private final OggettoRepository oggettoRepository;
    private final AzioneService azioneService;
    private final PagamentoFactory pagamentoFactory;
    private final AcquistoSubject acquistoSubject;

    @Transactional
    public AzioneResponse compra(
            Integer oggettoId,
            AcquistoRequest request,
            Utente utente
    ) {
        Oggetto oggetto = oggettoRepository.findByIdForUpdate(oggettoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Oggetto con id " + oggettoId + " non trovato"
                ));

        if (!oggetto.getStato().equals(StatoOggetto.DISPONIBILE)) {
            throw new ConflictException(
                    "L'oggetto con id " + oggettoId + " non è disponibile per l'acquisto"
            );
        }

        PagamentoTemplate pagamento = pagamentoFactory.get(request.getMetodoPagamento());

        pagamento.esegui(oggetto.getCosto(), utente);

        oggetto.setStato(StatoOggetto.VENDUTO);

        oggettoRepository.save(oggetto);

        AzioneResponse response = azioneService.creaAzioneAcquisto(oggetto, request, utente);

        acquistoSubject.notificaAcquisto(oggetto, utente);

        return response;
    }
}
package com.example.sitoartepsaw.service.facade;

import com.example.sitoartepsaw.dto.request.VenditaRequest;
import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Autore;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.repository.AutoreRepository;
import com.example.sitoartepsaw.repository.OggettoRepository;
import com.example.sitoartepsaw.repository.UtenteRepository;
import com.example.sitoartepsaw.repository.UtenteVerificatoRepository;
import com.example.sitoartepsaw.service.AzioneService;
import com.example.sitoartepsaw.service.oggettoFactory.OggettoFactory;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import com.example.sitoartepsaw.support.exceptions.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VenditaFacade {

    private final OggettoRepository oggettoRepository;
    private final AutoreRepository autoreRepository;
    private final UtenteVerificatoRepository utenteVerificatoRepository;
    private final AzioneService azioneService;
    private final UtenteRepository utenteRepository;
    private final OggettoFactory oggettoFactory;


    @Transactional
    public AzioneResponse vendi(
            VenditaRequest request,
            String email
    ) {
        Utente utente = utenteRepository.findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        Autore autore = null;

        if (request.getAutoreId() != null) {
            autore = autoreRepository.findById(request.getAutoreId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Autore con id " + request.getAutoreId() + " non trovato"
                    ));
        }
        if (!utenteVerificatoRepository.existsById(utente.getId())) {
            throw new UnauthorizedActionException(
                    "Solo gli utenti verificati possono mettere in vendita un'opera"
            );
        }

        Oggetto oggetto = oggettoFactory.crea(request, autore);
        Oggetto salvato = oggettoRepository.save(oggetto);

        return azioneService.creaAzioneVendita(salvato, utente);
    }
}
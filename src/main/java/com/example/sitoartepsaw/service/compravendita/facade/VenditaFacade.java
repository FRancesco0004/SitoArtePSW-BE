package com.example.sitoartepsaw.service.compravendita.facade;

import com.example.sitoartepsaw.dto.request.VenditaRequest;
import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Autore;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.entity.UtenteVerificato;
import com.example.sitoartepsaw.repository.AutoreRepository;
import com.example.sitoartepsaw.repository.OggettoRepository;
import com.example.sitoartepsaw.repository.UtenteRepository;
import com.example.sitoartepsaw.repository.UtenteVerificatoRepository;
import com.example.sitoartepsaw.service.azione.AzioneService;
import com.example.sitoartepsaw.service.oggetto.OggettoFactory;
import com.example.sitoartepsaw.support.exceptions.BadRequestException;
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

        if (!utenteVerificatoRepository.existsById(utente.getId())) {
            throw new UnauthorizedActionException(
                    "Solo gli utenti verificati possono mettere in vendita un'opera"
            );
        }

        Autore autore = recuperaOCreaAutore(request);

        Oggetto oggetto = oggettoFactory.crea(request, autore);
        Oggetto salvato = oggettoRepository.save(oggetto);

        return azioneService.creaAzioneVendita(salvato, utente);
    }

    private Autore recuperaOCreaAutore(VenditaRequest request) {
        String emailAutore = pulisci(request.getEmailAutore());
        String nomeAutore = pulisci(request.getNomeAutore());
        String cognomeAutore = pulisci(request.getCognomeAutore());

        if (emailAutore != null) {
            return recuperaOCreaAutoreVerificato(emailAutore);
        }

        boolean nomePresente = nomeAutore != null;
        boolean cognomePresente = cognomeAutore != null;

        if (!nomePresente && !cognomePresente) {
            return null;
        }

        if (!nomePresente || !cognomePresente) {
            throw new BadRequestException(
                    "Inserisci sia nome che cognome dell'autore oppure lascia entrambi vuoti"
            );
        }

        return autoreRepository
                .findByNomeIgnoreCaseAndCognomeIgnoreCase(nomeAutore, cognomeAutore)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Autore nuovoAutore = new Autore();
                    nuovoAutore.setNome(nomeAutore);
                    nuovoAutore.setCognome(cognomeAutore);
                    return autoreRepository.save(nuovoAutore);
                });
    }

    private Autore recuperaOCreaAutoreVerificato(String emailAutore) {
        UtenteVerificato utenteVerificato = utenteVerificatoRepository
                .findByUtente_EmailIgnoreCase(emailAutore)
                .orElseThrow(() -> new BadRequestException(
                        "Nessun utente verificato trovato con email " + emailAutore
                ));

        return autoreRepository
                .findByUtenteVerificato_Id(utenteVerificato.getId())
                .orElseGet(() -> recuperaAutoreLiberoOcreaNuovo(utenteVerificato));
    }

    private Autore recuperaAutoreLiberoOcreaNuovo(UtenteVerificato utenteVerificato) {
        String nome = utenteVerificato.getUtente().getNome();
        String cognome = utenteVerificato.getUtente().getCognome();

        Autore autoreLibero = autoreRepository
                .findByNomeIgnoreCaseAndCognomeIgnoreCase(nome, cognome)
                .stream()
                .filter(autore -> autore.getUtenteVerificato() == null)
                .findFirst()
                .orElse(null);

        if (autoreLibero != null) {
            autoreLibero.setUtenteVerificato(utenteVerificato);
            return autoreRepository.save(autoreLibero);
        }

        Autore nuovoAutore = new Autore();
        nuovoAutore.setNome(nome);
        nuovoAutore.setCognome(cognome);
        nuovoAutore.setUtenteVerificato(utenteVerificato);

        return autoreRepository.save(nuovoAutore);
    }

    private String pulisci(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
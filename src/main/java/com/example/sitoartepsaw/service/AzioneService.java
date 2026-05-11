package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Azione;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.enums.TipoAzione;
import com.example.sitoartepsaw.mapper.AzioneMapper;
import com.example.sitoartepsaw.repository.AzioneRepository;
import com.example.sitoartepsaw.repository.OggettoRepository;
import com.example.sitoartepsaw.support.exceptions.ConflictException;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;
import com.example.sitoartepsaw.support.exceptions.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.sitoartepsaw.dto.request.AcquistoRequest;
import com.example.sitoartepsaw.entity.Utente;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AzioneService {

    private final AzioneRepository azioneRepository;
    private final OggettoRepository oggettoRepository;
    private final AzioneMapper azioneMapper;

    public List<AzioneResponse> getStorico(Integer utenteId) {
        return azioneRepository
                .findByUtenteId(utenteId)
                .stream()
                .map(azioneMapper::toResponse)
                .toList();
    }

    public AzioneResponse getAzione(Integer azioneId, Integer utenteId) {
        Azione azione = azioneRepository.findById(azioneId)
                .orElseThrow(() -> new ResourceNotFoundException("Azione non trovata"));

        if (!azione.getUtente().getId().equals(utenteId)) {
            throw new UnauthorizedActionException("Non puoi accedere a questa azione perché appartiene a un altro utente");
        }

        return azioneMapper.toResponse(azione);
    }

    @Transactional
    public AzioneResponse annulla(Integer azioneId, Integer utenteId) {
        Azione azione = azioneRepository.findById(azioneId)
                .orElseThrow(() -> new ResourceNotFoundException("Azione non trovata"));

        if (!azione.getUtente().getId().equals(utenteId)) {
            throw new UnauthorizedActionException("Non puoi accedere a questa azione perché appartiene a un altro utente");
        }

        if (azione.getAnnullata()) {
            throw new ConflictException("L'azione con id " + azioneId + " è già stata annullata");
        }

        if (azione.getTipoAzione().equals(TipoAzione.COMPRA)) {
            Oggetto oggetto = azione.getOggetto();
            oggetto.setStato(StatoOggetto.DISPONIBILE);
            oggettoRepository.save(oggetto);
        }

        azione.setAnnullata(true);
        Azione salvata = azioneRepository.save(azione);
        return azioneMapper.toResponse(salvata);
    }

    @Transactional
    public AzioneResponse creaAzioneAcquisto(
            Oggetto oggetto,
            AcquistoRequest request,
            Utente utente
    ) {
        Azione azione = Azione.builder()
                .data(LocalDateTime.now())
                .tipoAzione(TipoAzione.COMPRA)
                .prezzoAlMomento(oggetto.getCosto())
                .metodoPagamento(request.getMetodoPagamento())
                .annullata(false)
                .utente(utente)
                .oggetto(oggetto)
                .build();

        Azione salvata = azioneRepository.save(azione);

        return azioneMapper.toResponse(salvata);
    }
}

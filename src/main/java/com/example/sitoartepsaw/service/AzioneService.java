package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Azione;
import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.enums.TipoAzione;
import com.example.sitoartepsaw.mapper.AzioneMapper;
import com.example.sitoartepsaw.repository.AzioneRepository;
import com.example.sitoartepsaw.repository.OggettoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .orElseThrow(() -> new RuntimeException("Azione non trovata"));

        if (!azione.getUtente().getId().equals(utenteId)) {
            throw new RuntimeException("Non autorizzato");
        }

        return azioneMapper.toResponse(azione);
    }

    @Transactional
    public AzioneResponse annulla(Integer azioneId, Integer utenteId) {
        Azione azione = azioneRepository.findById(azioneId)
                .orElseThrow(() -> new RuntimeException("Azione non trovata"));

        if (!azione.getUtente().getId().equals(utenteId)) {
            throw new RuntimeException("Non autorizzato");
        }

        if (azione.getAnnullata()) {
            throw new RuntimeException("Azione già annullata");
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
}

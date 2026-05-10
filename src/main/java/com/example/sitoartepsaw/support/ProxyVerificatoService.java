package com.example.sitoartepsaw.support;

import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("proxyVerificatoService")
@RequiredArgsConstructor
public class ProxyVerificatoService {

    private final UtenteRepository utenteRepository;

    public boolean isVerificato(Integer utenteId) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return utente.getUtenteVerificato() != null;
    }
}

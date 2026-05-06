package com.example.sitoartepsaw.support;

import com.example.sitoartepsaw.repository.UtenteVerificatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("proxyVerificatoService")
@RequiredArgsConstructor
public class ProxyVerificatoService {

    private final UtenteVerificatoRepository utenteVerificatoRepository;

    public boolean isVerificato(Integer utenteId) {
        return utenteVerificatoRepository.existsByUtenteId(utenteId);
    }
}

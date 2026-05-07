package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.dto.request.LoginRequest;
import com.example.sitoartepsaw.dto.request.RegistrazioneRequest;
import com.example.sitoartepsaw.dto.response.UtenteResponse;
import com.example.sitoartepsaw.entity.Utente;
import com.example.sitoartepsaw.mapper.UtenteMapper;
import com.example.sitoartepsaw.repository.UtenteRepository;
import com.example.sitoartepsaw.support.authentication.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // Concentro la comunicazione con la repository in questo punto

    public boolean existsByEmail(String email) {
        return utenteRepository.existsByEmail(email);
    }

    @Transactional
    public UtenteResponse registraUtente(RegistrazioneRequest request) {

        // Controlliamo se l'email è già in uso
        if (existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email già in uso: " + request.getEmail());
        }

        // Convertiamo DTO -> Entity
        Utente utente = utenteMapper.toEntity(request);

        // Criptazione delle password
        utente.setPassword(passwordEncoder.encode(request.getPassword()));
        Utente salvato = utenteRepository.save(utente);

        // restituiamo al frontend l'utente registrato
        return utenteMapper.toResponse(salvato);
    }

    @Transactional(readOnly = true) // piccola ottimizzazioone con Hibernate
    public String loginUtente(LoginRequest request) {

        // Autentichiamo l'utente, la classe UsernamePasswordAuthenticationToken è solo un
        // modulo da compilare da mandare poi all'authenticationManager nella classe
        // SecurityConfiguration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Da Authentication prendi l'utente
        Utente utente = (Utente) authentication.getPrincipal();

        // restituiamo il token
        return jwtTokenProvider.generateToken(utente);
    }

    public UtenteResponse getProfiloUtente(String email) {
        Utente utente = utenteRepository
                .findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Utente non trovato dalla mail"));

        return utenteMapper.toResponse(utente);
    }

    public UtenteResponse getUtenteById(Integer id){
        Utente utente = utenteRepository
                .findById(id)
                .orElseThrow( () -> new RuntimeException("Nessun utente associato all'ID "));

        return utenteMapper.toResponse(utente);
    }
}

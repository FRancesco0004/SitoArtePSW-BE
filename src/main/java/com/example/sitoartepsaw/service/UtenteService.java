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
import com.example.sitoartepsaw.support.exceptions.ConflictException;
import com.example.sitoartepsaw.support.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public boolean existsByEmail(String email) {
        return utenteRepository.existsByEmail(email);
    }

    @Transactional
    public UtenteResponse registraUtente(RegistrazioneRequest request) {

        if (existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    "Email già in uso: " + request.getEmail()
            );
        }

        Utente utente = utenteMapper.toEntity(request);

        utente.setPassword(passwordEncoder.encode(request.getPassword()));
        Utente salvato = utenteRepository.save(utente);

        return utenteMapper.toResponse(salvato);
    }

    @Transactional(readOnly = true)
    public String loginUtente(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Utente utente = (Utente) authentication.getPrincipal();

        return jwtTokenProvider.generateToken(utente);
    }

    @Transactional(readOnly = true)
    public UtenteResponse getProfiloUtente(String email) {
        Utente utente = utenteRepository
                .findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente con email " + email + " non trovato"
                ));

        return utenteMapper.toResponse(utente);
    }

    @Transactional(readOnly = true)
    public UtenteResponse getUtenteById(Integer id) {
        Utente utente = utenteRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente con id " + id + " non trovato"
                ));

        return utenteMapper.toResponse(utente);
    }
}
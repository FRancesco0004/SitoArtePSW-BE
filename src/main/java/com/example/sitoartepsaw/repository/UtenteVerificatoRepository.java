package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.UtenteVerificato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteVerificatoRepository extends JpaRepository<UtenteVerificato, Integer> {

    List<UtenteVerificato> findByTitolo(String titolo);

    Optional<UtenteVerificato> findByUtente_EmailIgnoreCase(String email);
}
package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.UtenteVerificato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtenteVerificatoRepository extends JpaRepository<UtenteVerificato, Integer> {
    List<UtenteVerificato> findByTitolo(String titolo);
}

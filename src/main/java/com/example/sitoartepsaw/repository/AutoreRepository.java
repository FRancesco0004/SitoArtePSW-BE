package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.Autore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoreRepository extends JpaRepository<Autore, Integer> {
    List<Autore> findByNome(String nome);
    List<Autore> findByCognome(String cognome);
    List<Autore> findByNomeIgnoreCaseAndCognomeIgnoreCase(String nome, String cognome);
    Optional<Autore> findByUtenteVerificato_Id(Integer utenteVerificatoId);

}

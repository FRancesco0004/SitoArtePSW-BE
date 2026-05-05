package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.Autore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoreRepository extends JpaRepository<Autore, Integer> {
    List<Autore> findByNome(String nome);
    List<Autore> findByCognome(String cognome);
    List<Autore> findByNomeAndCognome(String nome, String cognome);
}

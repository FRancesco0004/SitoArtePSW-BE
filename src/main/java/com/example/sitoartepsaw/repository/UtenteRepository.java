package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    //Useremo delle Derived Query
    List<Utente> findByNome(String nome);

    /*
    @Query("SELECT u FROM utenti u WHERE u.nome = :nomeDaCercare")
    List<Utente> trovaUtentiPerNome(@Param("nomeDaCercare") String nome);
     */

    List<Utente> findByCognome(String cognome);
    List<Utente> findByNomeAndCognome(String nome, String cognome);
    List<Utente> findByEmail(String email);
    boolean existsByEmail(String email);
}
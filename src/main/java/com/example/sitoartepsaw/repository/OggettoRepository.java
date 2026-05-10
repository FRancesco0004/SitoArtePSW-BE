package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.Oggetto;
import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.enums.TipoOpera;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OggettoRepository extends JpaRepository<Oggetto, Integer> {
    List<Oggetto> findByTitolo(String titolo);
    List<Oggetto> findByStato(StatoOggetto stato);
    List<Oggetto> findByTipoOpera(TipoOpera tipoOpera);
    List<Oggetto> findByAutoreId(Integer autoreId);
    List<Oggetto> findByStatoAndTipoOpera(StatoOggetto stato, TipoOpera tipoOpera);

    // Unica eccezione con @Query — la randomica serve alla Facade
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Ai fini dell'esame va bene ma con tanti utenti crea colli di bottiglia
    @Query("SELECT o FROM Oggetto o WHERE o.stato = 'DISPONIBILE' ORDER BY RAND()")
    List<Oggetto> findAllDisponibiliRandom();
}
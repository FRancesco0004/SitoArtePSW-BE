package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.Azione;
import com.example.sitoartepsaw.enums.TipoAzione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AzioneRepository extends JpaRepository<Azione, Integer> {
    List<Azione> findByUtenteId(Integer utenteId);
    List<Azione> findByOggettoId(Integer oggettoId);
    List<Azione> findByTipoAzione(TipoAzione tipoAzione);
    List<Azione> findByUtenteIdAndTipoAzione(Integer utenteId, TipoAzione tipoAzione);
    List<Azione> findByAnnullata(Boolean annullata);
    /*
    @Query("""
       SELECT a
       FROM Azione a
       WHERE a.oggetto.id = :oggettoId
       AND a.tipoAzione = :tipoAzione
       AND a.annullata = false
       ORDER BY a.data DESC
       """)
    */// sarebbe questa
    Optional<Azione> findFirstByOggettoIdAndTipoAzioneAndAnnullataFalseOrderByDataDesc(
            Integer oggettoId,
            TipoAzione tipoAzione
    );
}

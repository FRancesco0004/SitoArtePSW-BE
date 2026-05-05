package com.example.sitoartepsaw.repository;

import com.example.sitoartepsaw.entity.Azione;
import com.example.sitoartepsaw.enums.TipoAzione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AzioneRepository extends JpaRepository<Azione, Integer> {
    List<Azione> findByUtenteId(Integer utenteId);
    List<Azione> findByOggettoId(Integer oggettoId);
    List<Azione> findByTipoAzione(TipoAzione tipoAzione);
    List<Azione> findByUtenteIdAndTipoAzione(Integer utenteId, TipoAzione tipoAzione);
    List<Azione> findByAnnullata(Boolean annullata);
}

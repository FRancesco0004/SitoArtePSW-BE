package com.example.sitoartepsaw.mapper;

import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Azione;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AzioneMapper {

    @Mapping(source = "oggetto.titolo", target = "titoloOggetto")
    @Mapping(source = "utente.email", target = "emailUtente")
    AzioneResponse toResponse(Azione azione);

    /*
     public AzioneResponse toResponse(Azione azione) {
         return AzioneResponse.builder()
             .id(azione.getId())
             .data(azione.getData())
             .tipoAzione(azione.getTipoAzione())
             .prezzoAlMomento(azione.getPrezzoAlMomento())
             .metodoPagamento(azione.getMetodoPagamento())
             .annullata(azione.getAnnullata())
             .titoloOggetto(azione.getOggetto().getTitolo())
             .emailUtente(azione.getUtente().getEmail())
             .build();
     }
     */
}
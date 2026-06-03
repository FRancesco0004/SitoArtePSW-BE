package com.example.sitoartepsaw.mapper;

import com.example.sitoartepsaw.dto.response.AzioneResponse;
import com.example.sitoartepsaw.entity.Azione;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AzioneMapper {

    @Mapping(source = "oggetto.titolo", target = "titoloOggetto")
    @Mapping(source = "utente.email", target = "emailUtente")
    @Mapping(source = "scontoApplicato", target = "scontoApplicato")
    AzioneResponse toResponse(Azione azione);
}
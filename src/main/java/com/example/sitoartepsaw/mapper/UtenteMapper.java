package com.example.sitoartepsaw.mapper;

import com.example.sitoartepsaw.dto.request.RegistrazioneRequest;
import com.example.sitoartepsaw.dto.response.UtenteResponse;
import com.example.sitoartepsaw.entity.Utente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    //RegistrazioneRequest -> Entity Utente
    Utente toEntity(RegistrazioneRequest dto);

    //Utente Entity -> UtenteReponse
    UtenteResponse toResponse(Utente entity);

    /*
    @Component
     public class UtenteMapper {
    
         public Utente toEntity(RegistrazioneRequest dto) {
             return Utente.builder()
                 .nome(dto.getNome())
                 .cognome(dto.getCognome())
                 .email(dto.getEmail())
                 .password(dto.getPassword())
                 .build();
         }
    
         public UtenteResponse toResponse(Utente entity) {
             return UtenteResponse.builder()
                 .id(entity.getId())
                 .nome(entity.getNome())
                 .cognome(entity.getCognome())
                 .email(entity.getEmail())
                 .build();
         }
     }
     */
}
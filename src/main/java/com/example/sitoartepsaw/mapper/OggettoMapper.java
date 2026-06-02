package com.example.sitoartepsaw.mapper;

import com.example.sitoartepsaw.dto.response.OggettoAnteprimaResponse;
import com.example.sitoartepsaw.dto.response.OggettoDettaglioResponse;
import com.example.sitoartepsaw.dto.response.OggettoGiocoResponse;
import com.example.sitoartepsaw.entity.Oggetto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OggettoMapper {

    // Entity -> Anteprima (vengono mappati solo titolo e immagine)
    OggettoAnteprimaResponse toAnteprimaResponse(Oggetto oggetto);

    // Entity -> Dettaglio (è importante che si guidi il MapStruct perchè il nome e il cognome
    // dell'autore non stanno nella entity Oggetto ma in Autore)
    @Mapping(source = "autore.nome", target = "nomeAutore")
    @Mapping(source = "autore.cognome", target = "cognomeAutore")
    OggettoDettaglioResponse toDettaglioResponse(Oggetto oggetto);

    @Mapping(source = "autore.nome", target = "nomeAutore")
    @Mapping(source = "autore.cognome", target = "cognomeAutore")
    OggettoGiocoResponse toGiocoResponse(Oggetto oggetto);

    /*
     public OggettoAnteprimaResponse toAnteprimaResponse(Oggetto oggetto) {
         return OggettoAnteprimaResponse.builder()
             .id(oggetto.getId())
             .titolo(oggetto.getTitolo())
             .linkImmagine(oggetto.getLinkImmagine())
             .build();
     }

     public OggettoDettaglioResponse toDettaglioResponse(Oggetto oggetto) {
         return OggettoDettaglioResponse.builder()
             .id(oggetto.getId())
             .titolo(oggetto.getTitolo())
             .descrizione(oggetto.getDescrizione())
             .anno(oggetto.getAnno())
             .costo(oggetto.getCosto())
             .grandezza(oggetto.getGrandezza())
             .linkImmagine(oggetto.getLinkImmagine())
             .tipoOpera(oggetto.getTipoOpera())
             .peso(oggetto.getPeso())
             .nomeAutore(oggetto.getAutore().getNome())
             .cognomeAutore(oggetto.getAutore().getCognome())
             .build();
     }
     */
}
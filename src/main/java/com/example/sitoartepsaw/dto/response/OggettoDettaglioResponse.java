package com.example.sitoartepsaw.dto.response;

import com.example.sitoartepsaw.enums.TipoOpera;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OggettoDettaglioResponse {
    private Integer id;
    private String titolo;
    private String descrizione;
    private Integer anno;
    private BigDecimal costo;
    private String grandezza;
    private String linkImmagine;
    private TipoOpera tipoOpera;
    private BigDecimal peso;
    private String nomeAutore;
    private String cognomeAutore;
}
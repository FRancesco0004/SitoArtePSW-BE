package com.example.sitoartepsaw.dto.request;

import com.example.sitoartepsaw.enums.TipoOpera;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenditaRequest {

    @NotBlank(message = "Il titolo è obbligatorio")
    private String titolo;

    private String descrizione;

    private Integer anno;

    @NotNull(message = "Il costo è obbligatorio")
    @DecimalMin(value = "0.01", message = "Il costo deve essere maggiore di zero")
    private BigDecimal costo;

    private String grandezza;

    private String linkImmagine;

    @NotNull(message = "Il tipo opera è obbligatorio")
    private TipoOpera tipoOpera;

    private BigDecimal peso;

    private String nomeAutore;

    private String cognomeAutore;

    private String emailAutore;
}
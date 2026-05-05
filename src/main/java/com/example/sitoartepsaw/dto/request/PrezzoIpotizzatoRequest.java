package com.example.sitoartepsaw.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrezzoIpotizzatoRequest {

    @NotNull(message = "Il prezzo ipotizzato è obbligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Il prezzo deve essere positivo")
    private BigDecimal prezzoIpotizzato;
}
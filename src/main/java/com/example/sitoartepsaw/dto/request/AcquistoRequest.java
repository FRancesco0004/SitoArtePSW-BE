package com.example.sitoartepsaw.dto.request;

import com.example.sitoartepsaw.enums.MetodoPagamento;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AcquistoRequest {

    @NotNull(message = "Il metodo di pagamento è obbligatorio")
    private MetodoPagamento metodoPagamento;

    //Se prezzoStimato == null -> acquisto normale
    //altrimenti si verifica se si ha diritto allo sconto
    @DecimalMin(value = "0.01", message = "Il prezzo stimato deve essere maggiore di zero")
    private BigDecimal prezzoStimato;
}
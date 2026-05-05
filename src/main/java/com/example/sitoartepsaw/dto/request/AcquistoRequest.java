package com.example.sitoartepsaw.dto.request;

import com.example.sitoartepsaw.enums.MetodoPagamento;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AcquistoRequest {

    @NotNull(message = "Il metodo di pagamento è obbligatorio")
    private MetodoPagamento metodoPagamento;
}
package com.example.sitoartepsaw.dto.response;

import com.example.sitoartepsaw.enums.MetodoPagamento;
import com.example.sitoartepsaw.enums.TipoAzione;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Azione Response l'ho creato per lo storico degli acquisti
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AzioneResponse {
    private Integer id;
    private LocalDateTime data;
    private TipoAzione tipoAzione;
    private BigDecimal prezzoAlMomento;
    private MetodoPagamento metodoPagamento;
    //serve al FE per segnalare se si è vinta la sfida
    private Boolean scontoApplicato;
    private Boolean annullata;
    private String titoloOggetto;
    private String emailUtente;
}
package com.example.sitoartepsaw.entity;

import com.example.sitoartepsaw.enums.MetodoPagamento;
import com.example.sitoartepsaw.enums.TipoAzione;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "azioni")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@DynamicUpdate
public class Azione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_azione", nullable = false)
    private TipoAzione tipoAzione;

    @Basic
    @Column(name = "prezzo_al_momento", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoAlMomento;

    @Basic
    @Column(name = "sconto_applicato")
    private Boolean scontoApplicato;

    //La business logic la gestiamo dopo perchè metodo_pagamento può essere null in VENDE ma non in COMPRA
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = true)
    private MetodoPagamento metodoPagamento;

    @Basic
    @Column(name = "annullata", nullable = false)
    private Boolean annullata = false;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "oggetto_id", nullable = false)
    private Oggetto oggetto;
}
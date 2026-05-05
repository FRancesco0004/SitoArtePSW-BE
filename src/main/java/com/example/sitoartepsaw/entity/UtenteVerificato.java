package com.example.sitoartepsaw.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "utenti_verificati")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class UtenteVerificato {

    @Id
    @Column(name = "utente_id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "titolo", nullable = true, length = 150)
    private String titolo;

    @OneToOne
    @MapsId
    @JoinColumn(name = "utente_id")
    private Utente utente;
}

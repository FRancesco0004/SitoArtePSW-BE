package com.example.sitoartepsaw.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "autori")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class Autore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Basic
    @Column(name = "cognome", nullable = false, length = 100)
    private String cognome;

    @OneToOne
    @JoinColumn(name = "utente_verificato_id", unique = true, nullable = true)
    private UtenteVerificato utenteVerificato;
}
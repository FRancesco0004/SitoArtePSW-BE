package com.example.sitoartepsaw.entity;

import com.example.sitoartepsaw.enums.StatoOggetto;
import com.example.sitoartepsaw.enums.TipoOpera;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;

import java.math.BigDecimal;

@Entity
@Table(name = "oggetti")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@DynamicUpdate
public class Oggetto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @NaturalId
    @Column(name = "titolo", nullable = false, length = 255)
    private String titolo;

    @Basic
    @Column(name = "descrizione", nullable = true, columnDefinition = "TEXT")
    private String descrizione;

    @Basic
    @Column(name = "anno", nullable = true)
    private Integer anno;

    @Basic
    @Column(name = "costo", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @Basic
    @Column(name = "grandezza", nullable = true, length = 100)
    private String grandezza;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "link_immagine", nullable = true, length = 500)
    private String linkImmagine;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_opera", nullable = false)
    private TipoOpera tipoOpera;

    @Basic
    @Column(name = "peso", nullable = true, precision = 10, scale = 2)
    private BigDecimal peso;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private StatoOggetto stato = StatoOggetto.DISPONIBILE;

    @ManyToOne
    @JoinColumn(name = "autore_id", nullable = true)
    private Autore autore;
}
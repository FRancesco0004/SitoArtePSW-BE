package com.example.sitoartepsaw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "utenti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "utenteVerificato")
@ToString(exclude = "utenteVerificato")
@SQLRestriction("attivo = true")
public class Utente{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Basic
    @Column(name = "cognome", nullable = false, length = 50)
    private String cognome;

    @Basic
    @NaturalId
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Basic
    @Column(name = "attivo", nullable = false)
    private Boolean attivo = true;

    // Con mappedBy evitiamo una query separata sulla tabella utenti_verificati
    // perché navighiamo direttamente la relazione JPA già caricata in memoria.
    // FetchType.LAZY garantisce che UtenteVerificato venga caricato
    // solo quando effettivamente richiesto, evitando JOIN inutili
    // ogni volta che carichiamo un Utente.
    @OneToOne(mappedBy = "utente", fetch = FetchType.LAZY)
    private UtenteVerificato utenteVerificato;
}

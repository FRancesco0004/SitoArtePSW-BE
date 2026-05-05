package com.example.sitoartepsaw.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UtenteResponse {
    private Integer id;
    private String nome;
    private String cognome;
    private String email;
}
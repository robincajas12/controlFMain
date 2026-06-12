package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoVotacionDTO {
    private String id;
    private String titulo;
    private Long votosFavor;
    private Long votosContra;
    private Long votosAbstencion;
    private Double valorPrincipal;
    private String unitadPrincipal;
    private int escalaMinima;
    private int escalaMedia;
    private int escalaMaxima;
    private boolean tieneMenuOpciones;
}

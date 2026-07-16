package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de la votación de una ley, junto a los datos necesarios para
 * representarlo como un indicador visual (escala mínima/media/máxima).
 */
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

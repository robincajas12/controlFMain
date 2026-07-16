package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resumen histórico agregado de leyes y votos para el dashboard administrativo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteHistoricoDTO {
    private long totalLeyes;
    private long totalVotos;
    private long votosFavor;
    private long votosContra;
    private long votosAbstencion;
    private long leyesAprobadas;
    private long leyesEnDebate;
}

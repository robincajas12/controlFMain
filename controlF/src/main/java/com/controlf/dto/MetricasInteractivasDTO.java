package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Métricas de cumplimiento exploradas de forma interactiva: agregaciones
 * filtrables por categoría, estado y rango de fechas para alimentar
 * gráficos y comparaciones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricasInteractivasDTO {
    /** Filtros efectivamente aplicados, devueltos como eco para el frontend. */
    private String categoriaFiltro;
    private String estadoFiltro;
    private String desde;
    private String hasta;

    private long totalLeyes;
    private long totalVotos;
    private double promedioCoherenciaGlobal;

    private List<MetricaItemDTO> leyesPorEstado;
    private List<MetricaItemDTO> leyesPorCategoria;
    private List<MetricaItemDTO> votosPorTipo;
    private List<MetricaItemDTO> coherenciaPorCategoria;
    private List<MetricaItemDTO> serieVotosPorMes;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Seguimiento de un debate legislativo: incluye el texto oficial
 * (transcripción / exposición de motivos) y el resumen simplificado,
 * además del estado del debate y el resultado de la votación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebateLegislativoDTO {
    private String leyId;
    private String titulo;
    private String codigo;
    private String estado;
    private String categoria;
    private String proponente;
    private String fechaIngreso;
    /** Texto oficial ({@code descripcionOriginal}: transcripción / exposición de motivos). */
    private String resumenOficial;
    /** Resumen en lenguaje sencillo ({@code descripcionSimplificada}). */
    private String resumenSimplificado;
    private long votosFavor;
    private long votosContra;
    private long votosAbstencion;
    private long totalVotos;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento del calendario/agenda legislativa, derivado de fechas reales del
 * sistema: el ingreso de un expediente ({@code fechaIngreso}) o una
 * votación registrada ({@code fechaVoto}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoAgendaDTO {
    /** {@code "INGRESO_LEY"} o {@code "VOTACION"}. */
    private String tipo;
    /** Fecha en formato {@code yyyy-MM-dd}. */
    private String fecha;
    private String titulo;
    private String detalle;
    private String categoria;
    private String estado;
    private String leyId;
    /** Solo presente en eventos de tipo {@code "VOTACION"}. */
    private Long conteoVotos;
}

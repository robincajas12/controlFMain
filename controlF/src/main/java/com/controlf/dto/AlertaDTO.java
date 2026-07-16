package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alerta relevante para un usuario suscrito: una nueva ley o votación en
 * una de sus categorías. El indicador {@code nuevo} marca los ítems
 * posteriores a la fecha de suscripción.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {
    /** {@code "LEY"} o {@code "VOTACION"}. */
    private String tipo;
    private String titulo;
    private String categoria;
    /** Fecha en formato {@code yyyy-MM-dd}. */
    private String fecha;
    private String detalle;
    private String leyId;
    private boolean nuevo;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entrada de actividad reciente mostrada en el dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    /** P. ej. {@code "COMENTARIO"}, {@code "VOTO"}, {@code "LEY_NUEVA"}. */
    private String tipo;
    private String usuario;
    private String detalle;
    private String fecha;
}

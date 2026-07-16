package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vista de un comentario ciudadano para el panel de validación, incluyendo
 * el contexto (ley o político al que pertenece) para que el validador
 * decida aprobarlo, rechazarlo u observarlo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioModeracionDTO {
    private Integer id;
    private String texto;
    private String usuario;
    private String fecha;
    private String estado;
    private String notaModeracion;
    /** {@code "LEY"}, {@code "POLITICO"} o {@code "N/D"}. */
    private String contextoTipo;
    private String contextoTitulo;
    private String contextoId;
}

package com.controlf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Solicitud para publicar un comentario ciudadano.
 */
@Data
public class ComentarioRequestDTO {
    @NotBlank(message = "El texto del comentario es obligatorio")
    private String texto;

    /** Calificación opcional (1-5) enviada junto al comentario, para publicarla en el historial. */
    private Integer puntaje;
}

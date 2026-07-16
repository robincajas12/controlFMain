package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Debate ciudadano de una ley: puntuación promedio y comentarios públicos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebateCiudadanoDTO {
    private String id;
    private String titulo;
    private Double puntuacionPromedio;
    private Integer puntuacionMaxima;
    private List<ComentarioDebateDTO> comentarios;
    private String placeholderComentario;
    private boolean tieneBotonEnviar;
}

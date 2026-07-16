package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Auditoría de coherencia de una ley: por cada político vinculado a
 * través de una promesa de campaña, su voto y el nivel de coherencia resultante.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaCoherenciaDTO {
    private String id;
    private String titulo;
    private String subtitulo;
    private List<FilaAuditoriaDTO> filas;
    private String textoVerMas;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entrada del historial de coherencia de un político: el voto real
 * emitido en una ley frente a lo esperado por una promesa vinculada.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCoherenciaDTO {
    private String leyTitulo;
    private String votoReal;
    /** Nombre de {@link com.controlf.db.schema.enums.NivelCoherencia}. */
    private String resultado;
    private String analisis;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Solicitud para corregir el registro de asistencia de un voto puntual.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaVotoRequestDTO {
    private Boolean asistencia;
}

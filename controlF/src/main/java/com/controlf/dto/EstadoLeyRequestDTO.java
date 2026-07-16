package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Solicitud para actualizar el estado del proceso legislativo de una ley.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoLeyRequestDTO {
    private String estado;
}

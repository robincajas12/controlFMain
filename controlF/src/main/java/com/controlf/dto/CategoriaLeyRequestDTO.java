package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Solicitud para actualizar la categoría asignada a una ley.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaLeyRequestDTO {
    private String categoria;
}

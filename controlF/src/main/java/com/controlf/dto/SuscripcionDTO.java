package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Suscripción de un usuario a alertas de una categoría de leyes (o de todas).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionDTO {
    private Integer id;
    /** {@code null} significa todas las categorías. */
    private String categoria;
    private String fechaCreacion;
}

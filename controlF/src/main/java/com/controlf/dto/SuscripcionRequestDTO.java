package com.controlf.dto;

import lombok.Data;

/**
 * Solicitud para crear una suscripción de alertas. {@code categoria} nula
 * o vacía significa todas las categorías.
 */
@Data
public class SuscripcionRequestDTO {
    private String categoria;
}

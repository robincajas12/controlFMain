package com.controlf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Solicitud para registrar una nueva promesa de campaña para un político.
 */
@Data
public class PromesaRequestDTO {
    @NotBlank(message = "La descripción de la promesa es obligatoria")
    private String descripcion;

    @NotBlank(message = "La categoría de la promesa es obligatoria")
    private String categoria;
}

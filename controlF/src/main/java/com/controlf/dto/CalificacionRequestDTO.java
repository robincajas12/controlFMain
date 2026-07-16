package com.controlf.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Solicitud para registrar una calificación ciudadana (escala 1-5).
 */
@Data
public class CalificacionRequestDTO {
    @NotNull(message = "El puntaje es obligatorio")
    @Min(value = 1, message = "El puntaje mínimo es 1")
    @Max(value = 5, message = "El puntaje máximo es 5")
    private Integer puntaje;
}

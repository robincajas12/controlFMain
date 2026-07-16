package com.controlf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Solicitud para crear un vínculo de coherencia entre una promesa de campaña y una ley.
 */
@Data
public class VinculoRequestDTO {
    @NotNull(message = "La promesa es obligatoria")
    private Integer promesaId;

    @NotNull(message = "La ley es obligatoria")
    private Integer leyId;

    /** Nombre de {@link com.controlf.db.schema.enums.ImpactoEsperado}. */
    @NotBlank(message = "El impacto esperado es obligatorio")
    private String impactoEsperado;

    /** Nombre de {@link com.controlf.db.schema.enums.NivelCoherencia}. */
    @NotBlank(message = "El nivel de coherencia es obligatorio")
    private String nivelCoherencia;

    @NotBlank(message = "El análisis es obligatorio")
    private String analisis;
}

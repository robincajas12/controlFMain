package com.controlf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * Solicitud para registrar manualmente una nueva ley.
 */
@Data
public class CrearLeyRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El código de expediente es obligatorio")
    private String codigo;

    private String tipoExpediente;
    private String proponente;
    private String descripcionOriginal;
    private String descripcionSimplificada;
    private String impactoSocial;
    private String categoria;
    /** Nombre de {@link com.controlf.db.schema.enums.EstadoLey}; por defecto {@code DEBATE}. */
    private String estado;
    private LocalDate fechaIngreso;
}

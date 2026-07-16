package com.controlf.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Solicitud para registrar una nueva promesa de campaña.
 */
@Data
public class CrearPromesaRequestDTO {
    private Integer politicoId;
    private String descripcion;
    private String categoria;
    private LocalDate fechaPromesa;
}

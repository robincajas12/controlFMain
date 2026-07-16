package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entrada del historial de cambios de patrimonio/antecedentes de un
 * político, derivada del campo {@code historialActualizaciones} (JSON)
 * almacenado en la base de datos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCambioPerfilDTO {
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    private String fecha;
}

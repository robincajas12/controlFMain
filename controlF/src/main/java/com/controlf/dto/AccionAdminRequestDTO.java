package com.controlf.dto;

import lombok.Data;

/**
 * Solicitud genérica para ejecutar una acción administrativa parametrizada.
 */
@Data
public class AccionAdminRequestDTO {
    private String tipoAccion;
    private String payload;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Solicitud para registrar manualmente un nuevo político.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPoliticoRequestDTO {
    private String nombreCompleto;
    private String partidoPolitico;
    private String cargoActual;
    private String region;
    private String comision;
    private Boolean estaActivo;
    private BigDecimal patrimonioDeclarado;
    private String antecedentes;
    private String fotoUrl;
}

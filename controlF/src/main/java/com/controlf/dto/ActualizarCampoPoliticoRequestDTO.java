package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Solicitud para actualizar un único campo del perfil de un político
 * (por ejemplo, patrimonio o antecedentes).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCampoPoliticoRequestDTO {
    private String campo;
    private String valor;
}

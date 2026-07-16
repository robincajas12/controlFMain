package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Datos de referencia (políticos y leyes) para el panel administrativo del motor de coherencia.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotorCoherenciaDataDTO {
    private List<SimpleItemDTO> politicos;
    private List<SimpleItemDTO> leyes;
}

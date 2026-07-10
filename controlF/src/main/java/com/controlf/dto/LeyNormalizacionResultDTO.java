package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeyNormalizacionResultDTO {
    private int totalLeyes;
    private int leyesActualizadas;
    private int leyesSinCambios;
}

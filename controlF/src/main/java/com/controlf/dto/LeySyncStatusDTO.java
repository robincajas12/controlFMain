package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Progreso y resultado de sincronizar el detalle de votación de todas las leyes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeySyncStatusDTO {
    private int total;
    private int completed;
    private int imported;
    private int duplicated;
    private int ignored;
    private String currentLeyTitulo;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resumen de cuántas entradas del detalle de votación externo tienen un
 * político local homónimo, usado para previsualizar el alcance de una importación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingMatchSummaryDTO {
    private int found;
    private int notFound;
    private int total;
}

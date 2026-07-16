package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agenda legislativa: eventos ordenados por fecha, con los totales que los resumen.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaLegislativaDTO {
    private List<EventoAgendaDTO> eventos;
    private long totalEventos;
    private long totalIngresos;
    private long totalVotaciones;
}

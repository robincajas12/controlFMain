package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Perfil completo de una ley, agregando las distintas secciones que
 * conforman su vista de detalle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilLeyDTO {
    private ContenidoLeyDTO contenido;
    private ResultadoVotacionDTO votacion;
    private AuditoriaCoherenciaDTO auditoria;
    private DebateCiudadanoDTO debate;
    private VotingMatchSummaryDTO votingMatchSummary;
}

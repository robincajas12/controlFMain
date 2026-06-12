package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilLeyDTO {
    private ContenidoLeyDTO contenido;
    private ResultadoVotacionDTO votacion;
    private AuditoriaCoherenciaDTO auditoria;
    private DebateCiudadanoDTO debate;
}

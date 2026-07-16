package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vista resumida de una ley para listados de expedientes legislativos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpedienteLegislativoDTO {
    private String id;
    private String codigoExpediente;
    private String titulo;
    /** Alias de {@code titulo} esperado por el frontend. */
    private String tituloLey;
    private String categoria;
    private String estado;
    private boolean estaAprobado;
    private String proponente;
    private String accionUrl;
}

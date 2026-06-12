package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpedienteLegislativoDTO {
    private String id;
    private String codigoExpediente;
    private String titulo;
    private String tituloLey; // Alias para frontend
    private String categoria;
    private String estado;
    private boolean estaAprobado;
    private String proponente;
    private String accionUrl;
}

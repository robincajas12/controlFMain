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
public class PerfilPoliticoDTO {
    private String id;
    private String nombre;
    private String organizacion;
    private String cargo;
    private String patrimonio;
    private String fotoUrl;
    private String antecedentes;
    private boolean estaActivo;
    private Double porcentajeCoherencia;
    private String estadoEtiqueta;
    private List<HistorialCoherenciaDTO> historial;
    private List<ComentarioDebateDTO> comentarios;
}

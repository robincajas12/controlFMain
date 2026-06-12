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
public class AuditoriaCoherenciaDTO {
    private String id;
    private String titulo;
    private String subtitulo;
    private List<FilaAuditoriaDTO> filas;
    private String textoVerMas;
}

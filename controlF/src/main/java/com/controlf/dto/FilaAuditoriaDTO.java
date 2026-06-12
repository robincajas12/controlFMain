package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilaAuditoriaDTO {
    private String id;
    private String nombre;
    private String fotoUrl;
    private String bloque;
    private String voto;
    private String analisisCoherencia;
    private String nivelCoherencia;
}

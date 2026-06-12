package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCoherenciaDTO {
    private String leyTitulo;
    private String votoReal;
    private String resultado; // Coherente, Incoherente, Ambiguo
    private String analisis;
}

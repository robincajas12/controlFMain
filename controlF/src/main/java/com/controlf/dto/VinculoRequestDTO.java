package com.controlf.dto;

import lombok.Data;

@Data
public class VinculoRequestDTO {
    private Integer promesaId;
    private Integer leyId;
    private String impactoEsperado; // POSITIVO / NEGATIVO
    private String nivelCoherencia; // CUMPLE / INCUMPLE / AMBIGUO
    private String analisis;
}

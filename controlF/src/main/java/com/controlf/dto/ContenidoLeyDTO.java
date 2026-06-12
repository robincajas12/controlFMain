package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoLeyDTO {
    private String id;
    private String titulo;
    private String resumenEjecutivo;
    private String impactoSocial;
}

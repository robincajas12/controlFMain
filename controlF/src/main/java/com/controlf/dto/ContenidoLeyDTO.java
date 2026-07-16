package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contenido explicativo de una ley: resumen ejecutivo e impacto social,
 * junto a su estado y categoría reales (tal como están en la base de
 * datos) para dar trazabilidad en el perfil.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoLeyDTO {
    private String id;
    private String titulo;
    private String resumenEjecutivo;
    private String impactoSocial;
    private String estado;
    private String categoria;
}

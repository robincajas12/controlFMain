package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vista resumida de un político para listados en forma de tarjeta ("carta").
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartaPoliticoDTO {
    private String id;
    private String nombre;
    private String organizacion;
    private String fotoUrl;
    private boolean estaActivo;
    private String estadoEtiqueta;
    private Double porcentajeCoherencia;
    private Long cantidadProyectos;
}

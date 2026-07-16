package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Ley votada por dos o más de los políticos comparados. El mapa
 * {@code votos} asocia el id del político con su voto ({@code "FAVOR"},
 * {@code "CONTRA"}, {@code "ABSTENCION"} o {@code "—"} si no votó).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparacionLeyDTO {
    private String leyId;
    private String leyTitulo;
    private Map<String, String> votos;
    /** {@code true} si todos los que votaron coincidieron en el sentido del voto. */
    private boolean coinciden;
}

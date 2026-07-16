package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resultado de comparar los patrones de voto entre dos o más políticos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparacionVotosDTO {
    private List<ComparacionPoliticoDTO> politicos;
    private List<ComparacionLeyDTO> leyesComparadas;
    private long leyesEnComun;
    /** Leyes en común donde todos los políticos comparados coincidieron en el sentido del voto. */
    private long coincidencias;
    /** Porcentaje de coincidencia (0-100). */
    private double indiceCoincidencia;
}

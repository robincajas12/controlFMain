package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Página de políticos para el listado paginado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrillaPoliticosDTO {
    private String id;
    private List<CartaPoliticoDTO> cartas;
    private int paginaActual;
    private int totalPaginas;
}

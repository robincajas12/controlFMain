package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Página de leyes para el listado paginado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrillaLeyesDTO {
    private String id;
    private List<ExpedienteLegislativoDTO> leyes;
    private int paginaActual;
    private int totalPaginas;
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de una operación de importación desde la fuente externa:
 * cuántos registros se encontraron, importaron, ignoraron o eran duplicados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {
    private int found;
    private int imported;
    private int ignored;
    private int duplicates;
}

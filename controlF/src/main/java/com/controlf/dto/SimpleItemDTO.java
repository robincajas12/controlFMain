package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Par identificador-etiqueta genérico, usado en selectores y listados simples.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleItemDTO {
    private String id;
    private String label;
}

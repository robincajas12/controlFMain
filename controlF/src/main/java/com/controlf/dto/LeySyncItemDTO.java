package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ley candidata a sincronización con la fuente externa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeySyncItemDTO {
    private Integer id;
    private String titulo;
    private Long externalId;
}

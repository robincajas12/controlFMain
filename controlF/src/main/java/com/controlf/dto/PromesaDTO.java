package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromesaDTO {
    private Integer id;
    private String descripcion;
    private String categoria;
    private LocalDate fechaCreacion;
    private Integer politicoId;
}

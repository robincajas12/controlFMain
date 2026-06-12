package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltrosPoliticoDTO {
    private List<String> partidos;
    private List<String> regiones;
    private List<String> comisiones;
}

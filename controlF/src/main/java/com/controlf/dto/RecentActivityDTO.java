package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private String tipo; // COMENTARIO, VOTO, LEY_NUEVA
    private String usuario;
    private String detalle;
    private String fecha;
}

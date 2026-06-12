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
public class PanelControlDTO {
    private String tituloSeccion;
    private List<OpcionPanelDTO> opciones;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpcionPanelDTO {
        private String nombreOpcion;
        private String icono;
        private Integer notificacionBadge;
    }
}

package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Sección del panel de control de seguridad y usuarios, con sus opciones disponibles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanelControlDTO {
    private String tituloSeccion;
    private List<OpcionPanelDTO> opciones;

    /**
     * Opción individual dentro de una sección del panel de control.
     */
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

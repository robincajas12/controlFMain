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
public class PanelMantenimientoDTO {
    private String id;
    private String titulo;
    private String codigoReferencia;
    private boolean estadoBaseDeDatos;
    private String estadoEtiqueta;
    private String fechaUltimoRespaldo;
    private Integer cargaServidorPorcentaje;
    private List<String> accionesDisponibles;
}

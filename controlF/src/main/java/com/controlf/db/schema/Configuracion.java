package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Par clave-valor de configuración del sistema (p. ej. los umbrales de
 * coherencia usados por {@code PoliticoService}).
 */
@Entity
@Table(name = "configuracion_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion {

    @Id
    private String clave;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String valor;

    private String descripcion;
}

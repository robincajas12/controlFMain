package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Acción administrativa disponible en el panel de mantenimiento (p. ej.
 * {@code BACKUP}, {@code CACHE_CLEAR}), con un indicador de si está habilitada.
 */
@Entity
@Table(name = "acciones_disponibles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private Boolean estaHabilitada;
}

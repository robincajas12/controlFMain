package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Suscripción de un usuario para recibir alertas de nuevas leyes o
 * votaciones. Si {@code categoria} es nula, el usuario recibe alertas de
 * todas las categorías.
 */
@Entity
@Table(name = "suscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Categoría suscrita; {@code null} significa todas las categorías. */
    private String categoria;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fija la fecha de creación al momento actual si no se especificó una.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}

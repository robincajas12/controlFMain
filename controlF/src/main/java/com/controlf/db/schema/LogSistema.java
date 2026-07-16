package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Registro de auditoría de una acción administrativa (creado por
 * {@code AdminService#registrarLog}).
 */
@Entity
@Table(name = "logs_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String accion;

    @Column(columnDefinition = "TEXT")
    private String detalles;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}

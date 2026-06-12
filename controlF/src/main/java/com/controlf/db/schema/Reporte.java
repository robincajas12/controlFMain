package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    private EstadoReporte estado;

    @Column(nullable = false)
    private LocalDateTime fechaReporte;

    @ManyToOne
    @JoinColumn(name = "usuario_reportado_id", nullable = false)
    private Usuario usuarioReportado;

    @ManyToOne
    @JoinColumn(name = "reportero_id")
    private Usuario reportero;

    public enum EstadoReporte {
        PENDIENTE,
        REVISADO,
        DESESTIMADO
    }
}

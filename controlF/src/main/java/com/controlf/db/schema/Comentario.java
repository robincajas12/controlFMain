package com.controlf.db.schema;

import com.controlf.db.schema.enums.EstadoModeracion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Comentario ciudadano sobre una ley o un político, sujeto a moderación
 * por el rol {@code VALIDADOR}.
 */
@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String texto;

    private Boolean esBasadoEnHechos;
    private LocalDateTime fecha;

    /** Calificación ciudadana (1-5) asociada al comentario, mostrada junto a él en el historial. */
    private Integer puntaje;

    /**
     * Estado de moderación gestionado por el rol VALIDADOR. Por defecto
     * APROBADO, para que el contenido se publique de inmediato salvo que
     * un validador decida despublicarlo.
     */
    @Enumerated(EnumType.STRING)
    private EstadoModeracion estado = EstadoModeracion.APROBADO;

    /** Observación opcional que deja el validador al revisar el comentario. */
    @Column(columnDefinition = "TEXT")
    private String notaModeracion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * Constructor de compatibilidad anterior a la introducción de la
     * moderación: conserva la firma usada por el seeder y fija el estado
     * en APROBADO para no alterar el comportamiento existente.
     *
     * @param id identificador del comentario
     * @param texto contenido del comentario
     * @param esBasadoEnHechos indicador informativo sin efecto en la moderación
     * @param fecha fecha de publicación
     * @param usuario autor del comentario
     */
    public Comentario(Integer id, String texto, Boolean esBasadoEnHechos, LocalDateTime fecha, Usuario usuario) {
        this.id = id;
        this.texto = texto;
        this.esBasadoEnHechos = esBasadoEnHechos;
        this.fecha = fecha;
        this.usuario = usuario;
        this.estado = EstadoModeracion.APROBADO;
    }
}

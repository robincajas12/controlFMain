package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Perfil de un actor político: datos generales, promesas de campaña,
 * votos emitidos, comentarios ciudadanos, calificaciones y el historial
 * de cambios realizados sobre su perfil.
 */
@Entity
@Table(name = "politicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Politico {

    /**
     * Constructor de compatibilidad que omite {@code historialActualizaciones},
     * inicializándolo en {@code null}.
     *
     * @param id identificador del político
     * @param nombreCompleto nombre completo
     * @param partidoPolitico partido o movimiento político
     * @param cargoActual cargo que ocupa actualmente
     * @param region región a la que representa
     * @param comision comisión legislativa a la que pertenece
     * @param estaActivo si el político está actualmente en funciones
     * @param patrimonioDeclarado patrimonio declarado
     * @param antecedentes antecedentes relevantes
     * @param fotoUrl URL de la fotografía de perfil
     * @param promesas promesas de campaña asociadas
     * @param votos votos emitidos
     * @param comentarios comentarios ciudadanos recibidos
     * @param calificaciones calificaciones ciudadanas recibidas
     */
    public Politico(Integer id, String nombreCompleto, String partidoPolitico, String cargoActual, String region,
                    String comision, Boolean estaActivo, BigDecimal patrimonioDeclarado, String antecedentes,
                    String fotoUrl, List<Promesa> promesas, List<Voto> votos, List<Comentario> comentarios,
                    List<Calificacion> calificaciones) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.partidoPolitico = partidoPolitico;
        this.cargoActual = cargoActual;
        this.region = region;
        this.comision = comision;
        this.estaActivo = estaActivo;
        this.patrimonioDeclarado = patrimonioDeclarado;
        this.antecedentes = antecedentes;
        this.fotoUrl = fotoUrl;
        this.promesas = promesas;
        this.votos = votos;
        this.comentarios = comentarios;
        this.calificaciones = calificaciones;
        this.historialActualizaciones = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombreCompleto;

    private String partidoPolitico;
    private String cargoActual;
    private String region;
    private String comision;
    private Boolean estaActivo;
    private BigDecimal patrimonioDeclarado;

    @Column(columnDefinition = "TEXT")
    private String antecedentes;

    @Column(columnDefinition = "TEXT")
    private String historialActualizaciones;

    private String fotoUrl;

    @OneToMany(mappedBy = "politico", cascade = CascadeType.ALL)
    private List<Promesa> promesas;

    @OneToMany(mappedBy = "politico", cascade = CascadeType.ALL)
    private List<Voto> votos;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "politico_comentarios",
        joinColumns = @JoinColumn(name = "politico_id"),
        inverseJoinColumns = @JoinColumn(name = "comentario_id")
    )
    private List<Comentario> comentarios;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "politico_calificaciones",
        joinColumns = @JoinColumn(name = "politico_id"),
        inverseJoinColumns = @JoinColumn(name = "calificacion_id")
    )
    private List<Calificacion> calificaciones;
}

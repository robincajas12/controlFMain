package com.controlf.db.schema;

import com.controlf.db.schema.enums.ImpactoEsperado;
import com.controlf.db.schema.enums.NivelCoherencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vínculo de coherencia entre una promesa de campaña y una ley: registra
 * el impacto esperado, el nivel de coherencia resultante y el análisis
 * que lo respalda.
 */
@Entity
@Table(name = "vinculos_promesa_ley")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VinculoPromesaLey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ImpactoEsperado impactoEsperado;

    @Enumerated(EnumType.STRING)
    private NivelCoherencia nivelCoherencia;

    @Column(columnDefinition = "TEXT")
    private String analisisCoherencia;

    @ManyToOne
    @JoinColumn(name = "promesa_id", nullable = false)
    private Promesa promesa;

    @ManyToOne
    @JoinColumn(name = "ley_id", nullable = false)
    private Ley ley;
}

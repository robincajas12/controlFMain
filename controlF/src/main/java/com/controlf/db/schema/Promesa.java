package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "promesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String categoria;
    private LocalDate fechaCreacion;
    private LocalDate fechaPromesa;

    @ManyToOne
    @JoinColumn(name = "politico_id", nullable = false)
    private Politico politico;

    @OneToMany(mappedBy = "promesa", cascade = CascadeType.ALL)
    private List<VinculoPromesaLey> vinculos;
}
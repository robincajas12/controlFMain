package com.controlf.db.repository;

import com.controlf.db.schema.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
    List<Reporte> findByEstado(Reporte.EstadoReporte estado);

    long countByEstado(Reporte.EstadoReporte estado);
}

package com.controlf.db.repository;

import com.controlf.db.schema.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Acceso a los reportes de usuarios.
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

    /**
     * @param estado estado a filtrar
     * @return los reportes en ese estado
     */
    List<Reporte> findByEstado(Reporte.EstadoReporte estado);

    /**
     * @param estado estado a contar
     * @return la cantidad de reportes en ese estado
     */
    long countByEstado(Reporte.EstadoReporte estado);
}

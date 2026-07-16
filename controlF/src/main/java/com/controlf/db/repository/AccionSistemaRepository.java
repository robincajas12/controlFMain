package com.controlf.db.repository;

import com.controlf.db.schema.AccionSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Acceso a las acciones administrativas disponibles en el panel de mantenimiento.
 */
@Repository
public interface AccionSistemaRepository extends JpaRepository<AccionSistema, Integer> {

    /**
     * @return las acciones actualmente habilitadas
     */
    List<AccionSistema> findByEstaHabilitadaTrue();
}

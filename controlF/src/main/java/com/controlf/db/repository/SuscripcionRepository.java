package com.controlf.db.repository;

import com.controlf.db.schema.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a las suscripciones de alertas de leyes y votaciones.
 */
@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Integer> {

    /**
     * @param usuarioId identificador del usuario
     * @return las suscripciones registradas por ese usuario
     */
    List<Suscripcion> findByUsuarioId(Integer usuarioId);
}

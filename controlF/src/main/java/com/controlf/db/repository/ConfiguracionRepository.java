package com.controlf.db.repository;

import com.controlf.db.schema.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a los parámetros de configuración del sistema, indexados por su clave.
 */
@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {
}

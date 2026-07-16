package com.controlf.db.repository;

import com.controlf.db.schema.LogSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a los registros de auditoría de acciones administrativas.
 */
@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Integer> {
}

package com.controlf.db.repository;

import com.controlf.db.schema.AccionSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccionSistemaRepository extends JpaRepository<AccionSistema, Integer> {
    List<AccionSistema> findByEstaHabilitadaTrue();
}

package com.controlf.db.repository;

import com.controlf.db.schema.Promesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a las promesas de campaña.
 */
@Repository
public interface PromesaRepository extends JpaRepository<Promesa, Integer> {

    /**
     * @param politicoId identificador del político
     * @return las promesas de campaña registradas para ese político
     */
    java.util.List<Promesa> findByPoliticoId(Integer politicoId);
}

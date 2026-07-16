package com.controlf.db.repository;

import com.controlf.db.schema.VinculoPromesaLey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Acceso a los vínculos de coherencia entre promesas de campaña y leyes.
 */
@Repository
public interface VinculoPromesaLeyRepository extends JpaRepository<VinculoPromesaLey, Integer> {

    /**
     * Calcula el puntaje de coherencia promedio de un político, usando la
     * misma escala numérica (CUMPLE=100, AMBIGUO=50, INCUMPLE=0) que el
     * resto del sistema.
     *
     * @param politicoId identificador del político
     * @return el puntaje de coherencia promedio, o {@code null} si no tiene vínculos
     */
    @Query("SELECT AVG(CASE v.nivelCoherencia WHEN 'CUMPLE' THEN 100.0 WHEN 'AMBIGUO' THEN 50.0 ELSE 0.0 END) " +
           "FROM VinculoPromesaLey v WHERE v.promesa.politico.id = :politicoId")
    Double findAverageCoherenciaByPoliticoId(Integer politicoId);
}

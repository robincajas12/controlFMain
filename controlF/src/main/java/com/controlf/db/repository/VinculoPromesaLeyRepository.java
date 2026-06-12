package com.controlf.db.repository;

import com.controlf.db.schema.VinculoPromesaLey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VinculoPromesaLeyRepository extends JpaRepository<VinculoPromesaLey, Integer> {

    @Query("SELECT AVG(CASE v.nivelCoherencia WHEN 'CUMPLE' THEN 100.0 WHEN 'AMBIGUO' THEN 50.0 ELSE 0.0 END) " +
           "FROM VinculoPromesaLey v WHERE v.promesa.politico.id = :politicoId")
    Double findAverageCoherenciaByPoliticoId(Integer politicoId);
}

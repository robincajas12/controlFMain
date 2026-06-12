package com.controlf.db.repository;

import com.controlf.db.schema.Promesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromesaRepository extends JpaRepository<Promesa, Integer> {
    java.util.List<Promesa> findByPoliticoId(Integer politicoId);
}

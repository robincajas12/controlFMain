package com.controlf.db.repository;

import com.controlf.db.schema.Voto;
import com.controlf.db.schema.enums.TipoVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Acceso a los votos emitidos por políticos sobre leyes.
 */
@Repository
public interface VotoRepository extends JpaRepository<Voto, Integer> {

    /**
     * @param leyId identificador de la ley
     * @return los votos emitidos sobre esa ley
     */
    List<Voto> findByLeyId(Integer leyId);

    /**
     * @param politicoId identificador del político
     * @return los votos emitidos por ese político
     */
    List<Voto> findByPoliticoId(Integer politicoId);

    /**
     * @param politicoId identificador del político
     * @param leyId identificador de la ley
     * @return {@code true} si ese político ya tiene un voto registrado en esa ley
     */
    boolean existsByPoliticoIdAndLeyId(Integer politicoId, Integer leyId);

    /**
     * @param leyId identificador de la ley
     * @param tipoVoto sentido de voto a contar
     * @return la cantidad de votos de ese tipo en esa ley
     */
    long countByLeyIdAndTipoVoto(Integer leyId, TipoVoto tipoVoto);

    /**
     * @param tipoVoto sentido de voto a contar
     * @return la cantidad total de votos de ese tipo, en todas las leyes
     */
    long countByTipoVoto(TipoVoto tipoVoto);
}

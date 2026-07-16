package com.controlf.db.repository;

import com.controlf.db.schema.Comentario;
import com.controlf.db.schema.enums.EstadoModeracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a los comentarios ciudadanos y su estado de moderación.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    /**
     * @param estado estado de moderación a filtrar
     * @return los comentarios con ese estado, del más reciente al más antiguo
     */
    List<Comentario> findByEstadoOrderByFechaDesc(EstadoModeracion estado);

    /**
     * @param estado estado de moderación a contar
     * @return la cantidad de comentarios con ese estado
     */
    long countByEstado(EstadoModeracion estado);
}

package com.controlf.db.repository;

import com.controlf.db.schema.Ley;
import com.controlf.db.schema.enums.EstadoLey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Acceso a las leyes, con soporte de filtros dinámicos vía {@link JpaSpecificationExecutor}.
 */
@Repository
public interface LeyRepository extends JpaRepository<Ley, Integer>, JpaSpecificationExecutor<Ley> {

    /**
     * @param codigo código de expediente
     * @return la ley con ese código, si existe
     */
    Optional<Ley> findByCodigo(String codigo);

    /**
     * @param externalId identificador en la fuente externa
     * @return {@code true} si ya existe una ley importada con ese identificador
     */
    boolean existsByExternalId(Long externalId);

    /**
     * @param externalId identificador en la fuente externa
     * @return la ley importada con ese identificador, si existe
     */
    Optional<Ley> findByExternalId(Long externalId);

    /**
     * @param proponente nombre del proponente
     * @return la cantidad de leyes propuestas por ese proponente
     */
    long countByProponente(String proponente);

    /**
     * @param estado estado a contar
     * @return la cantidad de leyes en ese estado
     */
    long countByEstado(EstadoLey estado);

    /**
     * @return las categorías de ley distintas registradas, excluyendo valores nulos
     */
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT l.categoria FROM Ley l WHERE l.categoria IS NOT NULL")
    java.util.List<String> findDistinctCategorias();

    /**
     * Búsqueda inversa: dado un comentario, resuelve a qué ley pertenece
     * (usada para dar contexto en el panel de validación).
     *
     * @param comentarioId identificador del comentario
     * @return la ley que contiene ese comentario, si existe
     */
    @org.springframework.data.jpa.repository.Query("SELECT l FROM Ley l JOIN l.comentarios c WHERE c.id = :comentarioId")
    Optional<Ley> findByComentarioId(Integer comentarioId);
}

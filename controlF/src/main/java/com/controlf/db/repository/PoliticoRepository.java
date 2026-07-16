package com.controlf.db.repository;

import com.controlf.db.schema.Politico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Acceso a los políticos, con soporte de filtros dinámicos vía {@link JpaSpecificationExecutor}.
 */
@Repository
public interface PoliticoRepository extends JpaRepository<Politico, Integer>, JpaSpecificationExecutor<Politico> {

    /**
     * @return los partidos políticos distintos registrados, excluyendo valores nulos
     */
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.partidoPolitico FROM Politico p WHERE p.partidoPolitico IS NOT NULL")
    java.util.List<String> findDistinctPartidos();

    /**
     * @return las regiones distintas registradas, excluyendo valores nulos
     */
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.region FROM Politico p WHERE p.region IS NOT NULL")
    java.util.List<String> findDistinctRegiones();

    /**
     * @return las comisiones distintas registradas, excluyendo valores nulos
     */
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.comision FROM Politico p WHERE p.comision IS NOT NULL")
    java.util.List<String> findDistinctComisiones();

    /**
     * @param nombre texto a buscar dentro del nombre completo (insensible a mayúsculas/minúsculas)
     * @return el primer político cuyo nombre completo contiene el texto dado
     */
    Optional<Politico> findByNombreCompletoContainingIgnoreCase(String nombre);

    /**
     * Búsqueda inversa: dado un comentario, resuelve a qué político
     * pertenece (usada para dar contexto en el panel de validación).
     *
     * @param comentarioId identificador del comentario
     * @return el político que contiene ese comentario, si existe
     */
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Politico p JOIN p.comentarios c WHERE c.id = :comentarioId")
    Optional<Politico> findByComentarioId(Integer comentarioId);
}

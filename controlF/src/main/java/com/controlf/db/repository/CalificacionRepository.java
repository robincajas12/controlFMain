package com.controlf.db.repository;

import com.controlf.db.schema.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Acceso a las calificaciones ciudadanas sobre leyes y políticos.
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    /**
     * @param leyId identificador de la ley
     * @return el puntaje promedio de las calificaciones de esa ley, o {@code null} si no tiene ninguna
     */
    @Query("SELECT AVG(c.puntaje) FROM Calificacion c JOIN Ley l ON c MEMBER OF l.calificaciones WHERE l.id = :leyId")
    Double findAveragePuntajeByLeyId(Integer leyId);

    /**
     * @param politicoId identificador del político
     * @return el puntaje promedio de las calificaciones de ese político, o {@code null} si no tiene ninguna
     */
    @Query("SELECT AVG(c.puntaje) FROM Calificacion c JOIN Politico p ON c MEMBER OF p.calificaciones WHERE p.id = :politicoId")
    Double findAveragePuntajeByPoliticoId(Integer politicoId);

    /**
     * @param politicoId identificador del político
     * @return la cantidad de calificaciones recibidas por ese político
     */
    @Query("SELECT COUNT(c) FROM Calificacion c JOIN Politico p ON c MEMBER OF p.calificaciones WHERE p.id = :politicoId")
    long countByPoliticoId(Integer politicoId);
}

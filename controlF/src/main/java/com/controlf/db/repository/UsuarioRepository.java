package com.controlf.db.repository;

import com.controlf.db.schema.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Acceso a las cuentas de usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * @param email email de acceso
     * @return el usuario con ese email, si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * @param nombre nombre a verificar (insensible a mayúsculas/minúsculas)
     * @return {@code true} si ya existe un usuario con ese nombre
     */
    boolean existsByNombreIgnoreCase(String nombre);
}

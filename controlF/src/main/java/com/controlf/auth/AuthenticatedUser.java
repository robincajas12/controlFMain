package com.controlf.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Principal de Spring Security respaldado por un registro {@code Usuario}.
 * Envuelve los campos que Spring Security necesita para autenticación y
 * autorización, además de los datos de perfil adicionales
 * ({@link #getId()}, {@link #getNombre()}) que se usan en el resto de la
 * aplicación una vez autenticada la petición.
 */
public class AuthenticatedUser implements UserDetails {

    private final Integer id;
    private final String email;
    private final String nombre;
    private final String role;
    private final String passwordHash;
    private final boolean activo;

    /**
     * Crea el principal a partir de datos ya cargados desde la base de datos.
     *
     * @param id identificador del usuario en base de datos
     * @param email usuario de acceso (login)
     * @param nombre nombre para mostrar
     * @param role nombre del rol sin el prefijo {@code ROLE_} (p. ej. {@code "ADMIN"})
     * @param passwordHash hash BCrypt de la contraseña
     * @param activo indica si la cuenta está habilitada
     */
    public AuthenticatedUser(Integer id, String email, String nombre, String role, String passwordHash, boolean activo) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.role = role;
        this.passwordHash = passwordHash;
        this.activo = activo;
    }

    /**
     * @return el identificador del usuario autenticado en base de datos
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return el nombre para mostrar del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return el nombre del rol tal cual, sin el prefijo {@code ROLE_} usado
     *         por {@link #getAuthorities()}
     */
    public String getRole() {
        return role;
    }

    /**
     * Expone el rol del usuario como authority de Spring Security,
     * anteponiendo {@code ROLE_} tal como lo requieren las validaciones
     * {@code hasRole(...)}.
     *
     * @return una colección con la única authority correspondiente al rol
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    /**
     * @return el hash BCrypt de la contraseña, usado por Spring Security
     *         para validar credenciales
     */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * @return el email del usuario, usado como username de Spring Security
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * La expiración de cuentas no está modelada en esta aplicación, por lo
     * que las cuentas nunca expiran.
     *
     * @return siempre {@code true}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * El bloqueo de cuentas no está modelado en esta aplicación.
     *
     * @return siempre {@code true}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * La expiración de credenciales no está modelada en esta aplicación.
     *
     * @return siempre {@code true}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @return {@code true} si el {@code Usuario} subyacente está marcado como activo
     */
    @Override
    public boolean isEnabled() {
        return activo;
    }
}

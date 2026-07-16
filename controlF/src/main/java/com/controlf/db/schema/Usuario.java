package com.controlf.db.schema;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cuenta de usuario de la plataforma: credenciales, rol y las
 * publicaciones (comentarios, calificaciones) que ha realizado.
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones;

    /**
     * Constructor de compatibilidad que omite {@code activo}, creando la
     * cuenta como activa por defecto.
     *
     * @param id identificador del usuario
     * @param nombre nombre para mostrar
     * @param email email de acceso
     * @param passwordHash hash de la contraseña
     * @param avatarUrl URL del avatar
     * @param rol rol asignado
     * @param fechaRegistro fecha de registro
     * @param comentarios comentarios realizados por el usuario
     * @param calificaciones calificaciones realizadas por el usuario
     */
    public Usuario(Integer id, String nombre, String email, String passwordHash, String avatarUrl, Rol rol,
                   LocalDateTime fechaRegistro, List<Comentario> comentarios, List<Calificacion> calificaciones) {
        this(id, nombre, email, passwordHash, avatarUrl, rol, fechaRegistro, true, comentarios, calificaciones);
    }

    /**
     * @param id identificador del usuario
     * @param nombre nombre para mostrar
     * @param email email de acceso
     * @param passwordHash hash de la contraseña
     * @param avatarUrl URL del avatar
     * @param rol rol asignado
     * @param fechaRegistro fecha de registro
     * @param activo si la cuenta está habilitada
     * @param comentarios comentarios realizados por el usuario
     * @param calificaciones calificaciones realizadas por el usuario
     */
    public Usuario(Integer id, String nombre, String email, String passwordHash, String avatarUrl, Rol rol,
                   LocalDateTime fechaRegistro, boolean activo, List<Comentario> comentarios, List<Calificacion> calificaciones) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.avatarUrl = avatarUrl;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
        this.comentarios = comentarios;
        this.calificaciones = calificaciones;
    }

    /**
     * Fija la fecha de registro al momento actual si no se especificó una.
     */
    @PrePersist
protected void onCreate() {
    if (this.fechaRegistro == null) {
        this.fechaRegistro = LocalDateTime.now();
    }
}
    /**
     * Roles disponibles en la plataforma, usados por
     * {@link com.controlf.auth.SecurityConfig} para autorizar rutas.
     */
    public enum Rol {
        ADMIN,
        CIUDADANO,
        VALIDADOR
    }
}

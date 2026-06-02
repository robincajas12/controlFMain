package com.controlf.db.schema;

import java.time.LocalDateTime;

public class Usuario {

    private Integer id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String avatarUrl;
    private Rol rol;
    private LocalDateTime fechaRegistro;

    public enum Rol {
        ADMIN,
        VALIDADOR,
        CIUDADANO
    }
}

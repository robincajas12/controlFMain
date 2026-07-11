package com.controlf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRegisterRequestDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank
    private String nombre;

    /**
     * Rol opcional solicitado desde el formulario (uso de pruebas/desarrollo).
     * Si no se envía o no coincide con un rol existente, se asigna CIUDADANO.
     * No se crean roles nuevos: se reutiliza el enum {@link com.controlf.db.schema.Usuario.Rol}.
     */
    private String rol;
}

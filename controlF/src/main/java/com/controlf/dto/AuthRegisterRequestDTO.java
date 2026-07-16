package com.controlf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos enviados al registrar una nueva cuenta.
 */
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
     * Rol opcional solicitado desde el formulario de registro (pensado
     * para pruebas/desarrollo). Si no se envía o no coincide con un rol
     * existente, se asigna {@code CIUDADANO}; ver
     * {@link com.controlf.db.schema.Usuario.Rol}.
     */
    private String rol;
}

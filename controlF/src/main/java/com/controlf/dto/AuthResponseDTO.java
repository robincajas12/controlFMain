package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Respuesta de login/registro: el token de sesión emitido y los datos básicos del usuario.
 */
@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private Map<String, Object> user;
}

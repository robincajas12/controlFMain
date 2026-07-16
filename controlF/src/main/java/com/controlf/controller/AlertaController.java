package com.controlf.controller;

import com.controlf.auth.AuthenticatedUser;
import com.controlf.dto.AlertaDTO;
import com.controlf.dto.SuscripcionDTO;
import com.controlf.dto.SuscripcionRequestDTO;
import com.controlf.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Alertas y suscripciones a novedades de leyes y votaciones. Todos los
 * endpoints requieren un usuario autenticado, ya que las alertas y
 * suscripciones son siempre relativas al usuario de la sesión actual.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    /**
     * @param authentication sesión del usuario autenticado
     * @return las alertas pendientes para el usuario actual
     */
    @GetMapping
    public List<AlertaDTO> getAlertas(Authentication authentication) {
        return alertaService.obtenerAlertas(currentUserId(authentication));
    }

    /**
     * @param authentication sesión del usuario autenticado
     * @return las suscripciones a categorías registradas por el usuario actual
     */
    @GetMapping("/suscripciones")
    public List<SuscripcionDTO> getSuscripciones(Authentication authentication) {
        return alertaService.listarSuscripciones(currentUserId(authentication));
    }

    /**
     * Crea una suscripción a una categoría de leyes (o a todas si no se
     * especifica ninguna).
     *
     * @param request categoría a suscribir; puede ser {@code null}
     * @param authentication sesión del usuario autenticado
     * @return la suscripción creada
     */
    @PostMapping("/suscripciones")
    public SuscripcionDTO crearSuscripcion(@RequestBody(required = false) SuscripcionRequestDTO request,
                                           Authentication authentication) {
        String categoria = request != null ? request.getCategoria() : null;
        return alertaService.crearSuscripcion(currentUserId(authentication), categoria);
    }

    /**
     * Elimina una suscripción del usuario actual.
     *
     * @param id identificador de la suscripción
     * @param authentication sesión del usuario autenticado
     */
    @DeleteMapping("/suscripciones/{id}")
    public void eliminarSuscripcion(@PathVariable Integer id, Authentication authentication) {
        alertaService.eliminarSuscripcion(currentUserId(authentication), id);
    }

    /**
     * @param authentication sesión de Spring Security actual
     * @return el identificador del usuario autenticado
     */
    private Integer currentUserId(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getId();
    }
}

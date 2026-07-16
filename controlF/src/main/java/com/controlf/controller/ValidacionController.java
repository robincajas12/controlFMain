package com.controlf.controller;

import com.controlf.dto.ComentarioModeracionDTO;
import com.controlf.dto.ModeracionRequestDTO;
import com.controlf.service.ValidacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Panel de validación de contenido generado por ciudadanos (comentarios).
 * Accesible solo para los roles {@code VALIDADOR} y {@code ADMIN}
 * (ver {@link com.controlf.auth.SecurityConfig}).
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/validacion")
@RequiredArgsConstructor
public class ValidacionController {

    private final ValidacionService validacionService;

    /**
     * @param estado filtro opcional por estado de moderación
     * @return los comentarios pendientes o filtrados por estado
     */
    @GetMapping("/comentarios")
    public List<ComentarioModeracionDTO> listarComentarios(@RequestParam(required = false) String estado) {
        return validacionService.listarComentarios(estado);
    }

    /**
     * @return el conteo de comentarios agrupado por estado de moderación
     */
    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return validacionService.contarPorEstado();
    }

    /**
     * Aplica una decisión de moderación sobre un comentario.
     *
     * @param id identificador del comentario
     * @param request nuevo estado de moderación y nota opcional
     * @return el comentario con su estado de moderación actualizado
     */
    @PatchMapping("/comentarios/{id}/estado")
    public ComentarioModeracionDTO moderar(@PathVariable Integer id, @Valid @RequestBody ModeracionRequestDTO request) {
        return validacionService.moderar(id, request.getEstado(), request.getNota());
    }
}

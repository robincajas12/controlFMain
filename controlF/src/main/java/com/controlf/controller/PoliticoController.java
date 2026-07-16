package com.controlf.controller;

import com.controlf.auth.AuthenticatedUser;
import com.controlf.db.repository.ComentarioRepository;
import com.controlf.db.schema.Comentario;
import com.controlf.dto.ActualizarCampoPoliticoRequestDTO;
import com.controlf.dto.CalificacionRequestDTO;
import com.controlf.dto.ComentarioRequestDTO;
import com.controlf.dto.PromesaDTO;
import com.controlf.dto.PromesaRequestDTO;
import com.controlf.dto.SimpleItemDTO;
import com.controlf.service.PoliticoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Consulta y gestión de políticos: perfiles, listados filtrados,
 * comparación de patrones de voto, promesas de campaña, comentarios y
 * calificaciones ciudadanas. Las lecturas son públicas; las escrituras
 * están restringidas por rol en {@link com.controlf.auth.SecurityConfig}.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/politicos")
@RequiredArgsConstructor
public class PoliticoController {

    private final PoliticoService politicoService;
    private final ComentarioRepository comentarioRepository;

    /**
     * @param id identificador del político
     * @return el perfil completo del político
     */
    @GetMapping("/{id}")
    public com.controlf.dto.PerfilPoliticoDTO getPolitico(@PathVariable Integer id) {
        return politicoService.getPoliticoProfile(id);
    }

    /**
     * @return los valores disponibles (partidos, regiones, comisiones) para filtrar políticos
     */
    @GetMapping("/filtros")
    public com.controlf.dto.FiltrosPoliticoDTO getFiltros() {
        return politicoService.getFiltros();
    }

    /**
     * @return los políticos disponibles para importar desde la fuente externa
     */
    @GetMapping("/importables")
    public List<SimpleItemDTO> getImportables() {
        return politicoService.getPoliticosImportables();
    }

    /**
     * @param ids identificadores de los políticos a comparar
     * @return la comparación de patrones de voto entre los políticos indicados
     */
    @GetMapping("/comparar")
    public com.controlf.dto.ComparacionVotosDTO compararPatronesVoto(@RequestParam List<Integer> ids) {
        return politicoService.compararPatronesVoto(ids);
    }

    /**
     * @param pagina número de página (base 1)
     * @param size cantidad de resultados por página
     * @param nombre filtro opcional por nombre
     * @param partido filtro opcional por partido
     * @param region filtro opcional por región
     * @param comision filtro opcional por comisión
     * @return la página de políticos filtrados
     */
    @GetMapping
    public com.controlf.dto.GrillaPoliticosDTO getPoliticos(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String partido,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String comision) {
        return politicoService.getPoliticosFiltrados(pagina, size, nombre, partido, region, comision);
    }

    /**
     * Actualiza un campo puntual del perfil de un político.
     *
     * @param id identificador del político
     * @param request campo y nuevo valor a aplicar
     */
    @PatchMapping("/{id}")
    public void actualizarCampo(@PathVariable Integer id, @Valid @RequestBody ActualizarCampoPoliticoRequestDTO request) {
        politicoService.actualizarCampoPolitico(id, request);
    }

    /**
     * Publica un comentario ciudadano sobre un político.
     *
     * @param id identificador del político
     * @param request texto del comentario
     * @param authentication sesión del usuario autenticado
     */
    @PostMapping("/{id}/comentarios")
    public void postComentario(@PathVariable Integer id, @Valid @RequestBody ComentarioRequestDTO request, Authentication authentication) {
        Integer currentUserId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        politicoService.addComentario(id, request, currentUserId);
    }

    /**
     * Registra la calificación de un usuario sobre un político.
     *
     * @param id identificador del político
     * @param request valor de la calificación
     * @param authentication sesión del usuario autenticado
     */
    @PostMapping("/{id}/calificaciones")
    public void postCalificacion(@PathVariable Integer id, @Valid @RequestBody CalificacionRequestDTO request, Authentication authentication) {
        Integer currentUserId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        politicoService.addCalificacion(id, request, currentUserId);
    }

    /**
     * Registra una nueva promesa de campaña para un político. Requiere rol {@code ADMIN}.
     *
     * @param politicoId identificador del político
     * @param request datos de la promesa
     * @return la promesa creada, con estado 201
     */
    @PostMapping("/{politicoId}/promesas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromesaDTO> crearPromesa(@PathVariable Integer politicoId, @Valid @RequestBody PromesaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(politicoService.crearPromesa(politicoId, request));
    }

    /**
     * @param politicoId identificador del político
     * @return las promesas de campaña registradas para ese político
     */
    @GetMapping("/{politicoId}/promesas")
    public List<PromesaDTO> listarPromesas(@PathVariable Integer politicoId) {
        return politicoService.listarPromesasPorPolitico(politicoId);
    }

    /**
     * Edita un comentario existente. Solo el autor del comentario o un
     * administrador pueden modificarlo.
     *
     * @param comentarioId identificador del comentario
     * @param request nuevo texto del comentario
     * @param authentication sesión del usuario autenticado
     * @return 200 si se actualizó, 403 si el usuario no tiene permiso
     */
    @PatchMapping("/comentarios/{comentarioId}")
    public ResponseEntity<Void> actualizarComentario(@PathVariable Integer comentarioId, @Valid @RequestBody ComentarioRequestDTO request, Authentication authentication) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElseThrow();
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        if (!principal.getRole().equals("ADMIN") && !comentario.getUsuario().getId().equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        comentario.setTexto(request.getTexto());
        comentarioRepository.save(comentario);
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina un comentario existente. Solo el autor del comentario o un
     * administrador pueden eliminarlo.
     *
     * @param comentarioId identificador del comentario
     * @param authentication sesión del usuario autenticado
     * @return 200 si se eliminó, 403 si el usuario no tiene permiso
     */
    @DeleteMapping("/comentarios/{comentarioId}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Integer comentarioId, Authentication authentication) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElseThrow();
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        if (!principal.getRole().equals("ADMIN") && !comentario.getUsuario().getId().equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        comentarioRepository.delete(comentario);
        return ResponseEntity.ok().build();
    }
}

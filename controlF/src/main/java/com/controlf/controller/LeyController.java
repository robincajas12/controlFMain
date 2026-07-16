package com.controlf.controller;

import com.controlf.auth.AuthenticatedUser;
import com.controlf.db.repository.ComentarioRepository;
import com.controlf.db.schema.Comentario;
import com.controlf.dto.*;
import com.controlf.service.LeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Consulta y gestión de leyes: perfiles, listados filtrados, agenda
 * legislativa, debates, comentarios y calificaciones ciudadanas. Las
 * lecturas son públicas; las escrituras (categoría, estado, asistencia,
 * comentarios) están restringidas por rol en
 * {@link com.controlf.auth.SecurityConfig}.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/leyes")
@RequiredArgsConstructor
public class LeyController {

    private final LeyService leyService;
    private final ComentarioRepository comentarioRepository;

    /**
     * @param id identificador de la ley
     * @return el perfil completo de la ley (contenido, votos, comentarios, calificaciones)
     */
    @GetMapping("/{id}/perfil")
    public PerfilLeyDTO getPerfil(@PathVariable Integer id) {
        return leyService.getFullPerfilLey(id);
    }

    /**
     * Genera una explicación en lenguaje sencillo del contenido de la ley.
     *
     * @param id identificador de la ley
     * @return el contenido explicativo generado
     */
    @PostMapping("/{id}/explicar")
    public ContenidoLeyDTO explicar(@PathVariable Integer id) {
        return leyService.explicarLey(id);
    }

    /**
     * @return las categorías y estados disponibles para filtrar leyes
     */
    @GetMapping("/filtros")
    public FiltrosLeyDTO getFiltros() {
        return leyService.getFiltros();
    }

    /**
     * @return la agenda legislativa vigente
     */
    @GetMapping("/agenda")
    public AgendaLegislativaDTO getAgenda() {
        return leyService.getAgendaLegislativa();
    }

    /**
     * @param estado filtro opcional por estado del debate
     * @return los debates legislativos registrados, opcionalmente filtrados por estado
     */
    @GetMapping("/debates")
    public java.util.List<DebateLegislativoDTO> getDebates(@RequestParam(required = false) String estado) {
        return leyService.getDebatesLegislativos(estado);
    }

    /**
     * @param pagina número de página (base 1)
     * @param size cantidad de resultados por página
     * @param termino texto de búsqueda opcional
     * @param categoria filtro opcional por categoría
     * @param estado filtro opcional por estado
     * @return la página de leyes filtradas
     */
    @GetMapping
    public GrillaLeyesDTO getLeyes(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado) {
        return leyService.getLeyesFiltradas(pagina, size, termino, categoria, estado);
    }

    /**
     * Actualiza la categoría asignada a una ley.
     *
     * @param id identificador de la ley
     * @param request nueva categoría
     */
    @PatchMapping("/{id}/categoria")
    public void actualizarCategoria(@PathVariable Integer id, @Valid @RequestBody CategoriaLeyRequestDTO request) {
        leyService.actualizarCategoriaLey(id, request);
    }

    /**
     * Actualiza el estado del proceso legislativo de una ley.
     *
     * @param id identificador de la ley
     * @param request nuevo estado
     */
    @PatchMapping("/{id}/estado")
    public void actualizarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoLeyRequestDTO request) {
        leyService.actualizarEstadoLey(id, request);
    }

    /**
     * Corrige el registro de asistencia de un voto puntual.
     *
     * @param id identificador de la ley
     * @param votoId identificador del voto
     * @param request nuevo valor de asistencia
     */
    @PatchMapping("/{id}/votos/{votoId}/asistencia")
    public void actualizarAsistencia(@PathVariable Integer id, @PathVariable Integer votoId, @Valid @RequestBody AsistenciaVotoRequestDTO request) {
        leyService.actualizarAsistenciaVoto(id, votoId, request);
    }

    /**
     * Publica un comentario ciudadano sobre una ley.
     *
     * @param id identificador de la ley
     * @param request texto del comentario
     * @param authentication sesión del usuario autenticado
     */
    @PostMapping("/{id}/comentarios")
    public void postComentario(@PathVariable Integer id, @Valid @RequestBody ComentarioRequestDTO request, Authentication authentication) {
        Integer currentUserId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        leyService.addComentario(id, request, currentUserId);
    }

    /**
     * Registra la calificación de un usuario sobre una ley.
     *
     * @param id identificador de la ley
     * @param request valor de la calificación
     * @param authentication sesión del usuario autenticado
     */
    @PostMapping("/{id}/calificaciones")
    public void postCalificacion(@PathVariable Integer id, @Valid @RequestBody CalificacionRequestDTO request, Authentication authentication) {
        Integer currentUserId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        leyService.addCalificacion(id, request, currentUserId);
    }

    /**
     * Importa el detalle de votación de una ley desde la fuente externa.
     *
     * @param id identificador de la ley
     * @return el resultado de la importación
     */
    @PostMapping("/{id}/import-voting-detail")
    public ImportResultDTO importVotingDetail(@PathVariable Integer id) {
        return leyService.importVotingDetailVotes(id);
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

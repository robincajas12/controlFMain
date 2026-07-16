package com.controlf.service;

import com.controlf.db.repository.ComentarioRepository;
import com.controlf.db.repository.LeyRepository;
import com.controlf.db.repository.PoliticoRepository;
import com.controlf.db.schema.Comentario;
import com.controlf.db.schema.enums.EstadoModeracion;
import com.controlf.dto.ComentarioModeracionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Flujo de validación de contenido ciudadano. El rol {@code VALIDADOR}
 * puede listar los comentarios por estado y aprobar, rechazar u observar
 * cada uno. Solo el contenido con estado {@code APROBADO} se publica en
 * las vistas públicas (ver {@code PoliticoService#esComentarioPublico}).
 */
@Service
@RequiredArgsConstructor
public class ValidacionService {

    private final ComentarioRepository comentarioRepository;
    private final LeyRepository leyRepository;
    private final PoliticoRepository politicoRepository;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Lista los comentarios para moderar.
     *
     * @param estadoFiltro nombre del estado a filtrar; {@code null}, vacío
     *                      o {@code "TODOS"} devuelve todos los comentarios
     * @return los comentarios que coinciden con el filtro, ordenados por fecha descendente
     * @throws ResponseStatusException 400 si {@code estadoFiltro} no es un estado válido
     */
    public List<ComentarioModeracionDTO> listarComentarios(String estadoFiltro) {
        Stream<Comentario> comentarios;
        if (estadoFiltro == null || estadoFiltro.isBlank() || estadoFiltro.equalsIgnoreCase("TODOS")) {
            comentarios = comentarioRepository.findAll().stream();
        } else {
            EstadoModeracion estado = parseEstado(estadoFiltro);
            comentarios = comentarioRepository.findAll().stream()
                    .filter(c -> estadoEfectivo(c) == estado);
        }

        return comentarios
                .sorted((a, b) -> {
                    if (a.getFecha() == null || b.getFecha() == null) return 0;
                    return b.getFecha().compareTo(a.getFecha());
                })
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * @return el conteo de comentarios agrupado por estado de moderación,
     *         incluyendo estados sin comentarios (conteo en 0)
     */
    public java.util.Map<String, Long> contarPorEstado() {
        java.util.Map<String, Long> conteo = new java.util.LinkedHashMap<>();
        for (EstadoModeracion estado : EstadoModeracion.values()) {
            conteo.put(estado.name(), 0L);
        }
        for (Comentario c : comentarioRepository.findAll()) {
            String clave = estadoEfectivo(c).name();
            conteo.merge(clave, 1L, Long::sum);
        }
        return conteo;
    }

    /**
     * Aplica una decisión de moderación a un comentario.
     *
     * @param comentarioId identificador del comentario
     * @param nuevoEstado nombre del nuevo estado de moderación
     * @param nota nota de moderación opcional
     * @return el comentario con su estado actualizado
     * @throws ResponseStatusException 404 si el comentario no existe, 400
     *         si el estado no es válido
     */
    @Transactional
    public ComentarioModeracionDTO moderar(Integer comentarioId, String nuevoEstado, String nota) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));

        comentario.setEstado(parseEstado(nuevoEstado));
        if (nota != null && !nota.isBlank()) {
            comentario.setNotaModeracion(nota.trim());
        }
        comentarioRepository.save(comentario);
        return mapToDTO(comentario);
    }

    /**
     * @param c comentario a evaluar
     * @return el estado de moderación del comentario, tratando un estado
     *         nulo (contenido legado, previo a la introducción de
     *         moderación) como {@code APROBADO}
     */
    private EstadoModeracion estadoEfectivo(Comentario c) {
        return c.getEstado() == null ? EstadoModeracion.APROBADO : c.getEstado();
    }

    /**
     * @param valor nombre de estado a parsear
     * @return el {@link EstadoModeracion} correspondiente
     * @throws ResponseStatusException 400 si el valor no coincide con ningún estado conocido
     */
    private EstadoModeracion parseEstado(String valor) {
        try {
            return EstadoModeracion.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de moderación no válido: " + valor);
        }
    }

    /**
     * Resuelve a qué ley o político pertenece el comentario para incluir
     * ese contexto en el DTO de moderación.
     *
     * @param c comentario a convertir
     * @return el DTO de moderación con el contexto (ley o político) resuelto
     */
    private ComentarioModeracionDTO mapToDTO(Comentario c) {
        String contextoTipo = "N/D";
        String contextoTitulo = "";
        String contextoId = "";

        var leyOpt = leyRepository.findByComentarioId(c.getId());
        if (leyOpt.isPresent()) {
            contextoTipo = "LEY";
            contextoTitulo = leyOpt.get().getTitulo();
            contextoId = String.valueOf(leyOpt.get().getId());
        } else {
            var politicoOpt = politicoRepository.findByComentarioId(c.getId());
            if (politicoOpt.isPresent()) {
                contextoTipo = "POLITICO";
                contextoTitulo = politicoOpt.get().getNombreCompleto();
                contextoId = String.valueOf(politicoOpt.get().getId());
            }
        }

        return ComentarioModeracionDTO.builder()
                .id(c.getId())
                .texto(c.getTexto())
                .usuario(c.getUsuario() != null ? c.getUsuario().getNombre() : "Anónimo")
                .fecha(c.getFecha() != null ? c.getFecha().format(FORMATO_FECHA) : "")
                .estado(estadoEfectivo(c).name())
                .notaModeracion(c.getNotaModeracion())
                .contextoTipo(contextoTipo)
                .contextoTitulo(contextoTitulo)
                .contextoId(contextoId)
                .build();
    }
}

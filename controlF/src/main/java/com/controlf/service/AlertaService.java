package com.controlf.service;

import com.controlf.db.repository.LeyRepository;
import com.controlf.db.repository.SuscripcionRepository;
import com.controlf.db.repository.UsuarioRepository;
import com.controlf.db.repository.VotoRepository;
import com.controlf.db.schema.Ley;
import com.controlf.db.schema.Suscripcion;
import com.controlf.db.schema.Usuario;
import com.controlf.db.schema.Voto;
import com.controlf.dto.AlertaDTO;
import com.controlf.dto.SuscripcionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gestión de suscripciones y generación de alertas de nuevas leyes y
 * votaciones. Un usuario se suscribe a una categoría (o a todas) y
 * consulta las leyes y votaciones relevantes, resaltando las que son
 * nuevas desde su fecha de suscripción.
 */
@Service
@RequiredArgsConstructor
public class AlertaService {

    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeyRepository leyRepository;
    private final VotoRepository votoRepository;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int MAX_ALERTAS = 50;

    /**
     * @param userId identificador del usuario
     * @return las suscripciones a categorías registradas por el usuario
     */
    public List<SuscripcionDTO> listarSuscripciones(Integer userId) {
        return suscripcionRepository.findByUsuarioId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea una suscripción a una categoría (o a todas las leyes si la
     * categoría es nula o vacía). Si el usuario ya tiene una suscripción
     * equivalente, la devuelve en lugar de duplicarla.
     *
     * @param userId identificador del usuario
     * @param categoria categoría a la que suscribirse; {@code null} para suscribirse a todas
     * @return la suscripción creada o la ya existente equivalente
     * @throws ResponseStatusException 404 si el usuario no existe
     */
    @Transactional
    public SuscripcionDTO crearSuscripcion(Integer userId, String categoria) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String cat = (categoria != null && !categoria.isBlank()) ? categoria.trim() : null;

        for (Suscripcion existente : suscripcionRepository.findByUsuarioId(userId)) {
            if (mismaCategoria(existente.getCategoria(), cat)) {
                return mapToDTO(existente);
            }
        }

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setUsuario(usuario);
        suscripcion.setCategoria(cat);
        suscripcion.setFechaCreacion(LocalDateTime.now());
        return mapToDTO(suscripcionRepository.save(suscripcion));
    }

    /**
     * Elimina una suscripción, verificando que pertenezca al usuario que la solicita.
     *
     * @param userId identificador del usuario autenticado
     * @param suscripcionId identificador de la suscripción a eliminar
     * @throws ResponseStatusException 404 si la suscripción no existe, 403
     *         si pertenece a otro usuario
     */
    @Transactional
    public void eliminarSuscripcion(Integer userId, Integer suscripcionId) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suscripción no encontrada"));
        if (suscripcion.getUsuario() == null || !suscripcion.getUsuario().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar suscripciones de otro usuario");
        }
        suscripcionRepository.delete(suscripcion);
    }

    /**
     * Calcula las alertas de leyes nuevas y votaciones recientes que
     * coinciden con las suscripciones del usuario, marcando como
     * {@code nuevo} lo ocurrido después de la suscripción más antigua.
     * El resultado se ordena por fecha descendente y se acota a
     * {@link #MAX_ALERTAS}.
     *
     * @param userId identificador del usuario
     * @return las alertas relevantes para el usuario, o una lista vacía si no tiene suscripciones
     */
    public List<AlertaDTO> obtenerAlertas(Integer userId) {
        List<Suscripcion> subs = suscripcionRepository.findByUsuarioId(userId);
        if (subs.isEmpty()) {
            return List.of();
        }

        boolean todas = subs.stream().anyMatch(s -> s.getCategoria() == null || s.getCategoria().isBlank());
        Set<String> categorias = subs.stream()
                .map(Suscripcion::getCategoria)
                .filter(c -> c != null && !c.isBlank())
                .map(c -> c.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
        LocalDateTime desdeSuscripcion = subs.stream()
                .map(Suscripcion::getFechaCreacion)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        List<AlertaDTO> alertas = new ArrayList<>();
        for (Ley ley : leyRepository.findAll()) {
            boolean coincide = todas
                    || (ley.getCategoria() != null && categorias.contains(ley.getCategoria().toUpperCase(Locale.ROOT)));
            if (!coincide) {
                continue;
            }

            if (ley.getFechaIngreso() != null) {
                boolean nuevo = desdeSuscripcion != null
                        && !ley.getFechaIngreso().isBefore(desdeSuscripcion.toLocalDate());
                alertas.add(AlertaDTO.builder()
                        .tipo("LEY")
                        .titulo(ley.getTitulo())
                        .categoria(ley.getCategoria())
                        .fecha(ley.getFechaIngreso().toString())
                        .detalle("Nueva ley ingresada")
                        .leyId(ley.getId().toString())
                        .nuevo(nuevo)
                        .build());
            }

            List<Voto> votos = votoRepository.findByLeyId(ley.getId());
            LocalDateTime ultimaVotacion = votos.stream()
                    .map(Voto::getFechaVoto)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            if (ultimaVotacion != null) {
                boolean nuevo = desdeSuscripcion != null && !ultimaVotacion.isBefore(desdeSuscripcion);
                alertas.add(AlertaDTO.builder()
                        .tipo("VOTACION")
                        .titulo(ley.getTitulo())
                        .categoria(ley.getCategoria())
                        .fecha(ultimaVotacion.toLocalDate().toString())
                        .detalle("Votación registrada (" + votos.size() + " votos)")
                        .leyId(ley.getId().toString())
                        .nuevo(nuevo)
                        .build());
            }
        }

        alertas.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        return alertas.size() > MAX_ALERTAS ? new ArrayList<>(alertas.subList(0, MAX_ALERTAS)) : alertas;
    }

    /**
     * @param a primera categoría a comparar; puede ser {@code null} (equivale a "todas")
     * @param b segunda categoría a comparar; puede ser {@code null} (equivale a "todas")
     * @return {@code true} si ambas representan la misma categoría, sin distinguir mayúsculas/minúsculas
     */
    private boolean mismaCategoria(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

    /**
     * @param s entidad de suscripción a convertir
     * @return el DTO correspondiente, con la fecha formateada
     */
    private SuscripcionDTO mapToDTO(Suscripcion s) {
        return SuscripcionDTO.builder()
                .id(s.getId())
                .categoria(s.getCategoria())
                .fechaCreacion(s.getFechaCreacion() != null ? s.getFechaCreacion().format(FORMATO_FECHA) : null)
                .build();
    }
}

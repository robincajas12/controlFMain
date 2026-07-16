package com.controlf.service;

import com.controlf.db.repository.CalificacionRepository;
import com.controlf.db.repository.ComentarioRepository;
import com.controlf.db.repository.LeyRepository;
import com.controlf.db.repository.UsuarioRepository;
import com.controlf.db.repository.VotoRepository;
import com.controlf.db.schema.Calificacion;
import com.controlf.db.schema.Comentario;
import com.controlf.db.schema.Ley;
import com.controlf.db.schema.Usuario;
import com.controlf.db.schema.Voto;
import com.controlf.db.schema.enums.EstadoLey;
import com.controlf.db.schema.enums.TipoVoto;
import com.controlf.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Lógica de negocio para leyes: perfiles completos, listados filtrados y
 * paginados, agenda legislativa, seguimiento de debates, comentarios y
 * calificaciones ciudadanas, y sincronización del detalle de votación con
 * la fuente externa.
 */
@Service
@RequiredArgsConstructor
public class LeyService {

    private final LeyRepository leyRepository;
    private final VotoRepository votoRepository;
    private final CalificacionRepository calificacionRepository;
    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final com.controlf.db.repository.PoliticoRepository politicoRepository;
    private final GeminiService geminiService;

    private static final int CALIFICACION_MAXIMA = 5;

    /**
     * @return las categorías y estados disponibles para filtrar leyes
     */
    public FiltrosLeyDTO getFiltros() {
        return buildFiltrosLeyDTO();
    }

    /**
     * Busca leyes paginadas, aplicando de forma acumulativa los filtros de
     * texto libre (título o código), categoría y estado que se hayan
     * indicado. Ante cualquier error de consulta devuelve una página
     * vacía en lugar de propagar la excepción.
     *
     * @param pagina número de página (base 1)
     * @param size cantidad de resultados por página
     * @param termino texto de búsqueda opcional sobre título o código
     * @param categoria filtro opcional por categoría exacta
     * @param estado filtro opcional por estado (nombre del enum, insensible a mayúsculas)
     * @return la página de leyes resultante
     */
  public GrillaLeyesDTO getLeyesFiltradas(int pagina, int size, String termino, String categoria, String estado) {
    try {
        org.springframework.data.jpa.domain.Specification<Ley> spec = (root, query, cb) -> cb.conjunction();

        if (termino != null && !termino.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("titulo")), "%" + termino.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("codigo")), "%" + termino.toLowerCase() + "%")
            ));
        }

        if (categoria != null && !categoria.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("categoria"), categoria));
        }

        if (estado != null && !estado.isEmpty()) {
            try {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("estado"), EstadoLey.valueOf(estado.toUpperCase())));
            } catch (IllegalArgumentException e) {
                // Estado inválido: se ignora el filtro y se devuelve sin restringir por estado.
            }
        }

        org.springframework.data.domain.Page<Ley> page = leyRepository.findAll(
                spec,
                org.springframework.data.domain.PageRequest.of(
                        Math.max(0, pagina - 1),
                        size,
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC,
                                "id"
                        )
                )
        );

        List<ExpedienteLegislativoDTO> leyes = page.getContent().stream()
                .map(LeyService::mapToExpedienteDTO)
                .collect(Collectors.toList());

        return GrillaLeyesDTO.builder()
                .id("grilla-leyes")
                .leyes(leyes)
                .paginaActual(pagina)
                .totalPaginas(Math.max(1, page.getTotalPages()))
                .build();

    } catch (Exception e) {
        return GrillaLeyesDTO.builder()
                .id("grilla-leyes")
                .leyes(java.util.List.of())
                .paginaActual(1)
                .totalPaginas(1)
                .build();
    }
}
    /**
     * @param id identificador de la ley
     * @return el perfil completo de la ley: contenido, resultado de
     *         votación, auditoría de coherencia, debate ciudadano y
     *         resumen de coincidencia con el detalle de votación externo
     */
    public PerfilLeyDTO getFullPerfilLey(Integer id) {
        return PerfilLeyDTO.builder()
                .contenido(getContenidoLey(id))
                .votacion(getResultadoVotacion(id))
                .auditoria(getAuditoriaCoherencia(id))
                .debate(getDebateCiudadano(id))
                .votingMatchSummary(getVotingMatchSummary(id))
                .build();
    }

    /**
     * @return todas las leyes representadas como expedientes legislativos, sin paginar
     */
    public List<ExpedienteLegislativoDTO> getAllLeyesAsExpedientes() {
        return leyRepository.findAll().stream()
                .map(LeyService::mapToExpedienteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Construye la agenda legislativa a partir de eventos reales: el
     * ingreso de cada expediente y la última votación registrada por ley,
     * ordenados cronológicamente en forma descendente.
     *
     * @return la agenda con sus eventos y los totales de ingresos y votaciones
     */
    public AgendaLegislativaDTO getAgendaLegislativa() {
        List<EventoAgendaDTO> eventos = new ArrayList<>();
        long ingresos = 0;
        long votaciones = 0;

        for (Ley ley : leyRepository.findAll()) {
            String estado = ley.getEstado() != null ? ley.getEstado().name() : null;

            if (ley.getFechaIngreso() != null) {
                eventos.add(EventoAgendaDTO.builder()
                        .tipo("INGRESO_LEY")
                        .fecha(ley.getFechaIngreso().toString())
                        .titulo(ley.getTitulo())
                        .detalle("Ingreso del expediente legislativo")
                        .categoria(ley.getCategoria())
                        .estado(estado)
                        .leyId(ley.getId().toString())
                        .build());
                ingresos++;
            }

            List<Voto> votos = votoRepository.findByLeyId(ley.getId());
            LocalDateTime ultimaVotacion = votos.stream()
                    .map(Voto::getFechaVoto)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            if (ultimaVotacion != null) {
                eventos.add(EventoAgendaDTO.builder()
                        .tipo("VOTACION")
                        .fecha(ultimaVotacion.toLocalDate().toString())
                        .titulo(ley.getTitulo())
                        .detalle("Votación registrada en el pleno")
                        .categoria(ley.getCategoria())
                        .estado(estado)
                        .leyId(ley.getId().toString())
                        .conteoVotos((long) votos.size())
                        .build());
                votaciones++;
            }
        }

        // Orden cronológico descendente: las fechas ISO yyyy-MM-dd ordenan lexicográficamente.
        eventos.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

        return AgendaLegislativaDTO.builder()
                .eventos(eventos)
                .totalEventos(eventos.size())
                .totalIngresos(ingresos)
                .totalVotaciones(votaciones)
                .build();
    }

    /**
     * Lista las leyes en seguimiento de debate junto a su texto oficial
     * (transcripción / exposición de motivos), su resumen simplificado y
     * el resultado de la votación, ordenadas por fecha de ingreso
     * descendente (las leyes sin fecha quedan al final).
     *
     * @param estadoFiltro filtro opcional por estado; {@code "EN_DEBATE"}
     *                      agrupa los estados {@code DEBATE} y {@code EN_DEBATE}
     * @return los debates legislativos que coinciden con el filtro
     */
    public List<DebateLegislativoDTO> getDebatesLegislativos(String estadoFiltro) {
        return leyRepository.findAll().stream()
                .filter(ley -> coincideEstadoDebate(ley, estadoFiltro))
                .sorted((a, b) -> {
                    LocalDate fa = a.getFechaIngreso();
                    LocalDate fb = b.getFechaIngreso();
                    if (fa == null && fb == null) return 0;
                    if (fa == null) return 1;
                    if (fb == null) return -1;
                    return fb.compareTo(fa);
                })
                .map(this::mapToDebateDTO)
                .collect(Collectors.toList());
    }

    /**
     * @param ley ley a evaluar
     * @param estadoFiltro filtro de estado a aplicar; {@code null}, vacío
     *                      o {@code "TODOS"} coincide con cualquier estado
     * @return {@code true} si el estado de la ley coincide con el filtro
     */
    private boolean coincideEstadoDebate(Ley ley, String estadoFiltro) {
        if (estadoFiltro == null || estadoFiltro.isBlank() || estadoFiltro.equalsIgnoreCase("TODOS")) {
            return true;
        }
        if (estadoFiltro.equalsIgnoreCase("EN_DEBATE")) {
            return ley.getEstado() == EstadoLey.DEBATE || ley.getEstado() == EstadoLey.EN_DEBATE;
        }
        try {
            return ley.getEstado() == EstadoLey.valueOf(estadoFiltro.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    /**
     * @param ley ley a convertir
     * @return el DTO de debate legislativo con el desglose de votos de la ley
     */
    private DebateLegislativoDTO mapToDebateDTO(Ley ley) {
        long favor = votoRepository.countByLeyIdAndTipoVoto(ley.getId(), TipoVoto.FAVOR);
        long contra = votoRepository.countByLeyIdAndTipoVoto(ley.getId(), TipoVoto.CONTRA);
        long abstencion = votoRepository.countByLeyIdAndTipoVoto(ley.getId(), TipoVoto.ABSTENCION);

        return DebateLegislativoDTO.builder()
                .leyId(ley.getId().toString())
                .titulo(ley.getTitulo())
                .codigo(ley.getCodigo())
                .estado(ley.getEstado() != null ? ley.getEstado().name() : "SIN ESTADO")
                .categoria(ley.getCategoria())
                .proponente(ley.getProponente())
                .fechaIngreso(ley.getFechaIngreso() != null ? ley.getFechaIngreso().toString() : null)
                .resumenOficial(ley.getDescripcionOriginal())
                .resumenSimplificado(ley.getDescripcionSimplificada())
                .votosFavor(favor)
                .votosContra(contra)
                .votosAbstencion(abstencion)
                .totalVotos(favor + contra + abstencion)
                .build();
    }

    /**
     * Actualiza la categoría asignada a una ley.
     *
     * @param leyId identificador de la ley
     * @param request nueva categoría
     * @throws java.util.NoSuchElementException si la ley no existe
     */
    @Transactional
    public void actualizarCategoriaLey(Integer leyId, CategoriaLeyRequestDTO request) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        ley.setCategoria(request.getCategoria());
        leyRepository.save(ley);
    }

    /**
     * Actualiza el estado del proceso legislativo de una ley.
     *
     * @param leyId identificador de la ley
     * @param request nuevo estado (nombre del enum {@link EstadoLey})
     * @throws java.util.NoSuchElementException si la ley no existe
     * @throws IllegalArgumentException si el estado no es válido
     */
    @Transactional
    public void actualizarEstadoLey(Integer leyId, EstadoLeyRequestDTO request) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        ley.setEstado(EstadoLey.valueOf(request.getEstado().toUpperCase(Locale.ROOT)));
        leyRepository.save(ley);
    }

    /**
     * Corrige el registro de asistencia de un voto puntual, validando que
     * pertenezca a la ley indicada.
     *
     * @param leyId identificador de la ley
     * @param votoId identificador del voto
     * @param request nuevo valor de asistencia
     * @throws java.util.NoSuchElementException si la ley o el voto no existen
     * @throws IllegalArgumentException si el voto no pertenece a la ley indicada
     */
    @Transactional
    public void actualizarAsistenciaVoto(Integer leyId, Integer votoId, AsistenciaVotoRequestDTO request) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        Voto voto = votoRepository.findById(votoId).orElseThrow();
        if (!ley.getId().equals(voto.getLey().getId())) {
            throw new IllegalArgumentException("El voto no pertenece a la ley indicada");
        }
        voto.setAsistencia(request.getAsistencia());
        votoRepository.save(voto);
    }

    /**
     * Publica un comentario ciudadano sobre una ley.
     *
     * @param leyId identificador de la ley
     * @param request texto y puntaje del comentario
     * @param currentUserId identificador del usuario autor
     * @throws java.util.NoSuchElementException si la ley o el usuario no existen
     */
    public void addComentario(Integer leyId, ComentarioRequestDTO request, Integer currentUserId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        Usuario u = usuarioRepository.findById(currentUserId).orElseThrow();

        Comentario c = new Comentario();
        c.setTexto(request.getTexto());
        c.setUsuario(u);
        c.setFecha(LocalDateTime.now());
        c.setEsBasadoEnHechos(false);
        c.setPuntaje(request.getPuntaje());

        comentarioRepository.save(c);
        ley.getComentarios().add(c);
        leyRepository.save(ley);
    }

    /**
     * Registra la calificación de un usuario sobre una ley.
     *
     * @param leyId identificador de la ley
     * @param request valor de la calificación
     * @param currentUserId identificador del usuario autor
     * @throws java.util.NoSuchElementException si la ley o el usuario no existen
     */
    public void addCalificacion(Integer leyId, CalificacionRequestDTO request, Integer currentUserId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        Usuario u = usuarioRepository.findById(currentUserId).orElseThrow();

        Calificacion cal = new Calificacion();
        cal.setPuntaje(request.getPuntaje());
        cal.setUsuario(u);
        cal.setFecha(LocalDateTime.now());

        calificacionRepository.save(cal);
        ley.getCalificaciones().add(cal);
        leyRepository.save(ley);
    }

    /**
     * @param leyId identificador de la ley
     * @return el contenido explicativo de la ley (resumen, impacto social, estado, categoría)
     * @throws java.util.NoSuchElementException si la ley no existe
     */
    public ContenidoLeyDTO getContenidoLey(Integer leyId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        return ContenidoLeyDTO.builder()
                .id(ley.getId().toString())
                .titulo(ley.getTitulo())
                .resumenEjecutivo(ley.getDescripcionSimplificada())
                .impactoSocial(ley.getImpactoSocial())
                .estado(ley.getEstado() != null ? ley.getEstado().name() : "SIN ESTADO")
                .categoria(ley.getCategoria())
                .build();
    }

    /**
     * Genera (y cachea) una explicación en lenguaje sencillo del contenido
     * de la ley. Si ya existe una descripción simplificada, la devuelve
     * directamente sin volver a llamar al modelo de lenguaje.
     *
     * @param id identificador de la ley
     * @return el contenido explicativo, ya sea el existente o el recién generado
     * @throws java.util.NoSuchElementException si la ley no existe
     */
    @Transactional
    public ContenidoLeyDTO explicarLey(Integer id) {
        Ley ley = leyRepository.findById(id).orElseThrow();

        if (ley.getDescripcionSimplificada() != null && !ley.getDescripcionSimplificada().isBlank()) {
            return getContenidoLey(id);
        }

        String explicacion = geminiService.generarExplicacion(ley.getTitulo(), ley.getDescripcionOriginal());
        ley.setDescripcionSimplificada(explicacion);
        leyRepository.save(ley);

        return getContenidoLey(id);
    }

    /**
     * @param leyId identificador de la ley
     * @return el debate ciudadano de la ley, con su puntuación promedio y
     *         solo los comentarios que son públicos (ver
     *         {@link PoliticoService#esComentarioPublico})
     * @throws java.util.NoSuchElementException si la ley no existe
     */
    public DebateCiudadanoDTO getDebateCiudadano(Integer leyId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        Double avg = calificacionRepository.findAveragePuntajeByLeyId(leyId);

        return DebateCiudadanoDTO.builder()
                .id(ley.getId().toString())
                .titulo("Debate Ciudadano: " + ley.getTitulo())
                .puntuacionPromedio(avg != null ? avg : 0.0)
                .puntuacionMaxima(CALIFICACION_MAXIMA)
                .comentarios(ley.getComentarios().stream().filter(PoliticoService::esComentarioPublico).map(c -> ComentarioDebateDTO.builder()
                        .id(c.getId().toString())
                        .usuario(c.getUsuario().getNombre())
                        .mensaje(c.getTexto())
                        .fecha(c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .avatarUrl(c.getUsuario().getAvatarUrl())
                        .puntaje(c.getPuntaje())
                        .build()).collect(Collectors.toList()))
                .placeholderComentario("Escribe tu opinión sobre esta ley...")
                .tieneBotonEnviar(true)
                .build();
    }

    /**
     * @param leyId identificador de la ley
     * @return la auditoría de coherencia de la ley: por cada vínculo
     *         promesa-ley, el político, su voto en esta ley y el nivel de
     *         coherencia entre ambos
     * @throws java.util.NoSuchElementException si la ley no existe
     */
    public AuditoriaCoherenciaDTO getAuditoriaCoherencia(Integer leyId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        return AuditoriaCoherenciaDTO.builder()
                .id(ley.getId().toString())
                .titulo("Auditoría de Coherencia")
                .subtitulo("Cruce de datos para: " + ley.getTitulo())
                .filas(ley.getVinculos().stream().map(v -> FilaAuditoriaDTO.builder()
                        .id(v.getPromesa().getPolitico().getId().toString())
                        .nombre(v.getPromesa().getPolitico().getNombreCompleto())
                        .fotoUrl(v.getPromesa().getPolitico().getFotoUrl())
                        .bloque(v.getPromesa().getPolitico().getPartidoPolitico())
                        .voto(findVotoForPolitico(ley, v.getPromesa().getPolitico()))
                        .analisisCoherencia(v.getAnalisisCoherencia())
                        .nivelCoherencia(v.getNivelCoherencia().name().toLowerCase())
                        .build()).collect(Collectors.toList()))
                .textoVerMas("Ver detalles de la metodología de auditoría")
                .build();
    }

    /**
     * @param ley ley en la que se busca el voto
     * @param p político cuyo voto se busca
     * @return el nombre del tipo de voto emitido por el político en esa
     *         ley, o {@code "N/A"} si no votó
     */
    private String findVotoForPolitico(Ley ley, com.controlf.db.schema.Politico p) {
        return ley.getVotos().stream()
                .filter(v -> v.getPolitico().getId().equals(p.getId()))
                .map(v -> v.getTipoVoto().name())
                .findFirst()
                .orElse("N/A");
    }

    /**
     * @param leyId identificador de la ley
     * @return el resultado de la votación de la ley, con el desglose por tipo de voto
     * @throws java.util.NoSuchElementException si la ley no existe
     */
    public ResultadoVotacionDTO getResultadoVotacion(Integer leyId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        long favor = votoRepository.countByLeyIdAndTipoVoto(leyId, TipoVoto.FAVOR);
        long contra = votoRepository.countByLeyIdAndTipoVoto(leyId, TipoVoto.CONTRA);
        long abstencion = votoRepository.countByLeyIdAndTipoVoto(leyId, TipoVoto.ABSTENCION);
        long total = favor + contra + abstencion;

        return ResultadoVotacionDTO.builder()
                .id(ley.getId().toString())
                .titulo("Resultado Votación: " + ley.getTitulo())
                .votosFavor(favor)
                .votosContra(contra)
                .votosAbstencion(abstencion)
                .valorPrincipal((double) favor)
                .unitadPrincipal("Votos")
                .escalaMinima(0)
                .escalaMedia((int) (total > 0 ? total / 2 : 0))
                .escalaMaxima((int) total)
                .tieneMenuOpciones(true)
                .build();
    }

    /**
     * Importa el detalle de votación de una ley desde la fuente externa,
     * vinculando cada entrada al político local homónimo. Las entradas sin
     * político local coincidente se cuentan como ignoradas, y las que
     * ya tienen un voto registrado para esta ley, como duplicadas.
     * Como la fuente externa no informa la hora exacta del voto de cada
     * votante, se usa la fecha de ingreso de la ley (todos los votos de un
     * mismo expediente ocurren en la misma sesión); si esta faltara, se usa
     * el momento actual.
     *
     * @param leyId identificador de la ley
     * @return el resultado de la importación; si la ley no tiene
     *         identificador externo, devuelve un resultado vacío
     * @throws java.util.NoSuchElementException si la ley no existe
     * @throws RuntimeException si falla la consulta a la fuente externa
     */
    @Transactional
    public ImportResultDTO importVotingDetailVotes(Integer leyId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        if (ley.getExternalId() == null) {
            return new ImportResultDTO(0, 0, 0, 0);
        }

        java.util.List<VotingDetailDTO> details;
        try {
            details = loadVotingDetailEntries(ley.getExternalId());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar el detalle de votación externa: " + e.getMessage(), e);
        }

        int total = details.size();
        int imported = 0;
        int ignored = 0;
        int duplicates = 0;

        LocalDateTime fechaVotacion = ley.getFechaIngreso() != null
                ? ley.getFechaIngreso().atStartOfDay()
                : LocalDateTime.now();

        for (VotingDetailDTO detail : details) {
            String fullName = buildFullName(detail.getFirstName(), detail.getLastname());
            if (fullName.isEmpty()) {
                ignored++;
                continue;
            }

            var politicoOpt = politicoRepository.findByNombreCompletoContainingIgnoreCase(fullName);
            if (politicoOpt.isEmpty()) {
                ignored++;
                continue;
            }

            var politico = politicoOpt.get();
            if (votoRepository.existsByPoliticoIdAndLeyId(politico.getId(), ley.getId())) {
                duplicates++;
                continue;
            }

            Voto voto = new Voto();
            voto.setPolitico(politico);
            voto.setLey(ley);
            voto.setTipoVoto(mapVote(detail.getDescription()));
            voto.setAsistencia(true);
            voto.setFechaVoto(fechaVotacion);
            votoRepository.save(voto);
            imported++;
        }

        return new ImportResultDTO(total, imported, ignored, duplicates);
    }

    /**
     * Calcula cuántas entradas del detalle de votación externo tienen un
     * político local homónimo, sin importar nada. Útil para mostrar de
     * antemano qué tan completa sería una importación antes de ejecutarla.
     *
     * @param leyId identificador de la ley
     * @return el resumen de coincidencias encontradas/no encontradas; si
     *         la ley no tiene identificador externo o la consulta falla,
     *         devuelve un resumen vacío
     * @throws java.util.NoSuchElementException si la ley no existe
     */
    public VotingMatchSummaryDTO getVotingMatchSummary(Integer leyId) {
        Ley ley = leyRepository.findById(leyId).orElseThrow();
        if (ley.getExternalId() == null) {
            return VotingMatchSummaryDTO.builder()
                    .found(0)
                    .notFound(0)
                    .total(0)
                    .build();
        }

        try {
            java.util.List<VotingDetailDTO> details = loadVotingDetailEntries(ley.getExternalId());
            int total = details.size();
            int found = 0;

            for (VotingDetailDTO detail : details) {
                String fullName = buildFullName(detail.getFirstName(), detail.getLastname());
                if (fullName.isEmpty()) {
                    continue;
                }
                if (politicoRepository.findByNombreCompletoContainingIgnoreCase(fullName).isPresent()) {
                    found++;
                }
            }

            return VotingMatchSummaryDTO.builder()
                    .found(found)
                    .notFound(Math.max(0, total - found))
                    .total(total)
                    .build();
        } catch (Exception e) {
            return VotingMatchSummaryDTO.builder()
                    .found(0)
                    .notFound(0)
                    .total(0)
                    .build();
        }
    }

    /**
     * @param externalId identificador externo de la votación; puede ser {@code null}
     * @return el detalle de votación obtenido de la fuente externa, o una lista vacía si no hay identificador
     * @throws Exception si la petición HTTP o el parseo de la respuesta fallan
     */
    private java.util.List<VotingDetailDTO> loadVotingDetailEntries(Long externalId) throws Exception {
        if (externalId == null) {
            return java.util.List.of();
        }

        java.net.URI uri = java.net.URI.create("https://datos.asambleanacional.gob.ec/ecurul/assemblyman/votingDetail?idVoting=" + externalId);
        java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS)
                .build();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36")
                .GET()
                .build();

        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 || response.body() == null || response.body().isBlank()) {
            return java.util.List.of();
        }

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(java.util.List.class, VotingDetailDTO.class));
    }

    /**
     * @param firstName nombre; puede ser {@code null}
     * @param lastname apellido; puede ser {@code null}
     * @return el nombre completo, con espacios sobrantes recortados
     */
    private String buildFullName(String firstName, String lastname) {
        String trimmedFirst = firstName == null ? "" : firstName.trim();
        String trimmedLast = lastname == null ? "" : lastname.trim();
        return (trimmedFirst + " " + trimmedLast).trim();
    }

    /**
     * @param description descripción de voto recibida de la fuente externa (p. ej. {@code "SI"}, {@code "NO"})
     * @return el {@link TipoVoto} correspondiente, o {@code ABSTENCION} si es nula o desconocida
     */
    private TipoVoto mapVote(String description) {
        if (description == null) {
            return TipoVoto.ABSTENCION;
        }
        return switch (description.trim().toUpperCase()) {
            case "SI" -> TipoVoto.FAVOR;
            case "NO" -> TipoVoto.CONTRA;
            case "ABSTENCION", "ABSTENCIÓN" -> TipoVoto.ABSTENCION;
            default -> TipoVoto.ABSTENCION;
        };
    }

    /**
     * @return las categorías distintas registradas y todos los estados posibles de ley
     */
    private FiltrosLeyDTO buildFiltrosLeyDTO() {
        return FiltrosLeyDTO.builder()
                .categorias(leyRepository.findDistinctCategorias())
                .estados(java.util.Arrays.stream(EstadoLey.values()).map(estado -> estado.name()).collect(Collectors.toList()))
                .build();
    }

    /**
     * Clasifica una ley en una categoría temática a partir de palabras
     * clave presentes en su título o descripción.
     *
     * @param titulo título de la ley
     * @param descripcion descripción de la ley
     * @return la categoría detectada, o {@code "GENERAL"} si no coincide con ninguna palabra clave
     */
    private String classifyLaw(String titulo, String descripcion) {
        String combined = ((titulo == null ? "" : titulo) + " " + (descripcion == null ? "" : descripcion)).toLowerCase(Locale.ROOT);
        if (combined.contains("educ") || combined.contains("escuela") || combined.contains("universidad")) {
            return "EDUCACION";
        }
        if (combined.contains("salud") || combined.contains("hospital") || combined.contains("medic")) {
            return "SALUD";
        }
        if (combined.contains("trabajo") || combined.contains("empleo") || combined.contains("labor")) {
            return "TRABAJO";
        }
        if (combined.contains("seguridad") || combined.contains("polic") || combined.contains("delito")) {
            return "SEGURIDAD";
        }
        return "GENERAL";
    }

    /**
     * @param ley entidad a convertir
     * @return el expediente legislativo correspondiente a la ley
     */
    private static ExpedienteLegislativoDTO mapToExpedienteDTO(Ley ley) {
        return ExpedienteLegislativoDTO.builder()
                .id(ley.getId().toString())
                .codigoExpediente(ley.getCodigo())
                .titulo(ley.getTitulo())
                .tituloLey(ley.getTitulo())
                .categoria(ley.getCategoria())
                .estado(ley.getEstado().name())
                .estaAprobado(ley.getEstado() == EstadoLey.APROBADA)
                .proponente(ley.getProponente())
                .accionUrl("/leyes/" + ley.getId())
                .build();
    }

    /**
     * @param externalId identificador externo de la votación; puede ser {@code null}
     * @return la cantidad de entradas en el detalle de votación externo
     * @throws Exception si la petición HTTP o el parseo de la respuesta fallan
     */
    private int loadVotingDetailCount(Long externalId) throws Exception {
        if (externalId == null) {
            return 0;
        }
        java.net.URI uri = java.net.URI.create("https://datos.asambleanacional.gob.ec/ecurul/assemblyman/votingDetail?idVoting=" + externalId);
        java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS)
                .build();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36")
                .GET()
                .build();
        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 || response.body() == null || response.body().isBlank()) {
            return 0;
        }
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        java.util.List<?> list = mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(java.util.List.class, java.util.Map.class));
        return list == null ? 0 : list.size();
    }
}

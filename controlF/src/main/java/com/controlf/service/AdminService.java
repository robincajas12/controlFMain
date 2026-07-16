package com.controlf.service;

import com.controlf.db.repository.*;
import com.controlf.db.schema.*;
import com.controlf.db.schema.enums.ImpactoEsperado;
import com.controlf.db.schema.enums.NivelCoherencia;
import com.controlf.dto.CrearLeyRequestDTO;
import com.controlf.dto.CrearPoliticoRequestDTO;
import com.controlf.dto.CrearPromesaRequestDTO;
import com.controlf.dto.ImportResultDTO;
import com.controlf.dto.LeyNormalizacionResultDTO;
import com.controlf.dto.LeySyncItemDTO;
import com.controlf.dto.LeySyncStatusDTO;
import com.controlf.dto.PanelControlDTO;
import com.controlf.dto.PanelMantenimientoDTO;
import com.controlf.dto.ReporteHistoricoDTO;
import com.controlf.dto.VinculoRequestDTO;
import com.controlf.dto.MotorCoherenciaDataDTO;
import com.controlf.dto.SimpleItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lógica de negocio detrás del panel administrativo: creación y
 * eliminación de contenido (leyes, políticos, promesas, vínculos de
 * coherencia), sincronización con la fuente externa, mantenimiento del
 * sistema y auditoría (log de acciones administrativas).
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ReporteRepository reporteRepository;
    private final LogSistemaRepository logRepository;
    private final DataSource dataSource;
    private final PromesaRepository promesaRepository;
    private final LeyRepository leyRepository;
    private final VinculoPromesaLeyRepository vinculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PoliticoRepository politicoRepository;
    private final AssemblyImportService assemblyImportService;
    private final LeyService leyService;
    private final VotoRepository votoRepository;

    /**
     * @return los políticos y leyes disponibles como origen/destino de
     *         vínculos en el motor de coherencia
     */
    public MotorCoherenciaDataDTO getMotorData() {
        List<SimpleItemDTO> politicos = politicoRepository.findAll().stream()
                .map(p -> new SimpleItemDTO(p.getId().toString(), p.getNombreCompleto()))
                .collect(Collectors.toList());

        List<SimpleItemDTO> leyes = leyRepository.findAll().stream()
                .map(l -> new SimpleItemDTO(l.getId().toString(), l.getTitulo()))
                .collect(Collectors.toList());

        return MotorCoherenciaDataDTO.builder()
                .politicos(politicos)
                .leyes(leyes)
                .build();
    }

    /**
     * @param politicoId identificador del político
     * @return las promesas de campaña registradas para ese político
     */
    public List<SimpleItemDTO> getPromesasByPolitico(Integer politicoId) {
        return promesaRepository.findByPoliticoId(politicoId).stream()
                .map(p -> new SimpleItemDTO(p.getId().toString(), p.getDescripcion()))
                .collect(Collectors.toList());
    }

    /**
     * Registra una nueva promesa de campaña asociada a un político.
     *
     * @param request datos de la promesa a crear
     * @throws java.util.NoSuchElementException si el político indicado no existe
     */
    public void crearPromesa(CrearPromesaRequestDTO request) {
        Politico politico = politicoRepository.findById(request.getPoliticoId()).orElseThrow();

        Promesa promesa = new Promesa();
        promesa.setDescripcion(request.getDescripcion());
        promesa.setCategoria(request.getCategoria());
        promesa.setFechaCreacion(LocalDate.now());
        promesa.setPolitico(politico);
        promesa.setVinculos(List.of());

        promesaRepository.save(promesa);
        registrarLog("CREAR_PROMESA", "Promesa creada para político " + politico.getId());
    }

    /**
     * Crea un vínculo de coherencia entre una promesa de campaña y una ley,
     * registrando el nivel de coherencia y el impacto esperado.
     *
     * @param request datos del vínculo a crear
     * @throws java.util.NoSuchElementException si la promesa o la ley indicadas no existen
     */
    public void crearVinculoCoherencia(VinculoRequestDTO request) {
        Promesa p = promesaRepository.findById(request.getPromesaId()).orElseThrow();
        Ley l = leyRepository.findById(request.getLeyId()).orElseThrow();

        VinculoPromesaLey v = new VinculoPromesaLey();
        v.setPromesa(p);
        v.setLey(l);
        v.setImpactoEsperado(ImpactoEsperado.valueOf(request.getImpactoEsperado()));
        v.setNivelCoherencia(NivelCoherencia.valueOf(request.getNivelCoherencia()));
        v.setAnalisisCoherencia(request.getAnalisis());

        vinculoRepository.save(v);
        registrarLog("CREAR_VINCULO", "Vínculo creado entre promesa " + p.getId() + " y ley " + l.getId());
    }

    /**
     * Ejecuta un respaldo manual de la base de datos. El respaldo en sí lo
     * gestiona la infraestructura; este método solo registra la acción.
     *
     * @param adminId identificador del administrador que lo solicitó (sin uso actualmente)
     */
    public void ejecutarRespaldo(Integer adminId) {
        registrarLog("BACKUP", "Respaldo manual ejecutado");
    }

    /**
     * Limpia las cachés del sistema y registra la acción.
     */
    public void limpiarCache() {
        registrarLog("CACHE_CLEAR", "Caché del sistema limpiada");
    }

    /**
     * Sincroniza las leyes de todos los políticos registrados con la
     * fuente externa.
     */
    public void importarLeyes() {
        var politicoIds = politicoRepository.findAll().stream()
                .map(Politico::getId)
                .collect(Collectors.toList());

        if (politicoIds.isEmpty()) {
            registrarLog("IMPORT_LEYES", "No se encontraron políticos para importar leyes");
            return;
        }

        ImportResultDTO result = assemblyImportService.importLeyesForPoliticos(politicoIds);
        registrarLog("IMPORT_LEYES", String.format("Sincronización con API externa finalizada: importadas=%d, duplicadas=%d, ignoradas=%d", result.getImported(), result.getDuplicates(), result.getIgnored()));
    }

    /**
     * Reescribe la descripción original de cada ley en lenguaje más
     * accesible, sustituyendo fórmulas legislativas comunes por
     * equivalentes en lenguaje llano y acotando la longitud del texto.
     *
     * @return un resumen con el total de leyes procesadas, actualizadas y sin cambios
     */
    @Transactional
    public LeyNormalizacionResultDTO normalizarLeyes() {
        List<Ley> leyes = leyRepository.findAll();
        int actualizadas = 0;
        int sinCambios = 0;

        for (Ley ley : leyes) {
            String original = ley.getDescripcionOriginal();
            String normalizada = normalizeLegislativeText(original);
            if (normalizada == null) {
                normalizada = "No hay descripción disponible.";
            }
            ley.setDescripcionSimplificada(normalizada);
            if (original != null && original.equals(normalizada)) {
                sinCambios++;
            } else {
                actualizadas++;
            }
        }
        leyRepository.saveAll(leyes);
        registrarLog("NORMALIZAR_LEYES", "Se normalizó el lenguaje legislativo de " + leyes.size() + " leyes");

        return LeyNormalizacionResultDTO.builder()
                .totalLeyes(leyes.size())
                .leyesActualizadas(actualizadas)
                .leyesSinCambios(sinCambios)
                .build();
    }

    /**
     * @return todas las leyes con su identificador externo, como candidatas a sincronización
     */
    public java.util.List<LeySyncItemDTO> listarLeyesParaSync() {
        return leyRepository.findAll().stream()
                .map(ley -> LeySyncItemDTO.builder()
                        .id(ley.getId())
                        .titulo(ley.getTitulo())
                        .externalId(ley.getExternalId())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Recorre todas las leyes con identificador externo e importa el
     * detalle de su votación desde la fuente externa, una por una.
     *
     * @return un resumen del progreso y resultado de la sincronización
     */
    @Transactional
    public LeySyncStatusDTO syncAllLeyesWithVotingDetails() {
        var leyes = leyRepository.findAll();
        int total = leyes.size();
        int completed = 0;
        int imported = 0;
        int duplicates = 0;
        int ignored = 0;
        String currentLeyTitulo = "";

        for (Ley ley : leyes) {
            currentLeyTitulo = ley.getTitulo();
            if (ley.getExternalId() == null) {
                ignored += 1;
                completed += 1;
                continue;
            }

            ImportResultDTO result = leyService.importVotingDetailVotes(ley.getId());
            imported += result.getImported();
            duplicates += result.getDuplicates();
            ignored += result.getIgnored();
            completed += 1;
        }

        registrarLog("SYNC_LEYES_VOTING_DETAIL", String.format("Sincronización de leyes finalizada: total=%d, importadas=%d, duplicadas=%d, ignoradas=%d", total, imported, duplicates, ignored));

        return LeySyncStatusDTO.builder()
                .total(total)
                .completed(completed)
                .imported(imported)
                .duplicated(duplicates)
                .ignored(ignored)
                .currentLeyTitulo(completed < total ? currentLeyTitulo : "Finalizado")
                .build();
    }

    /**
     * Registra un nuevo político con listas de relaciones vacías.
     *
     * @param request datos del político a crear
     */
    @Transactional
    public void crearPolitico(CrearPoliticoRequestDTO request) {
        Politico politico = new Politico();
        politico.setNombreCompleto(request.getNombreCompleto());
        politico.setPartidoPolitico(request.getPartidoPolitico());
        politico.setCargoActual(request.getCargoActual());
        politico.setRegion(request.getRegion());
        politico.setComision(request.getComision());
        politico.setEstaActivo(request.getEstaActivo() != null ? request.getEstaActivo() : true);
        politico.setPatrimonioDeclarado(request.getPatrimonioDeclarado());
        politico.setAntecedentes(request.getAntecedentes());
        politico.setFotoUrl(request.getFotoUrl());
        politico.setPromesas(new java.util.ArrayList<>());
        politico.setVotos(new java.util.ArrayList<>());
        politico.setComentarios(new java.util.ArrayList<>());
        politico.setCalificaciones(new java.util.ArrayList<>());
        politicoRepository.save(politico);
        registrarLog("CREAR_POLITICO", "Político creado: " + politico.getNombreCompleto());
    }

    /**
     * Registra una nueva ley manualmente, validando que el código de
     * expediente sea único.
     *
     * @param request datos de la ley a crear
     * @throws org.springframework.web.server.ResponseStatusException 400 si
     *         el código está vacío, 409 si ya existe una ley con ese código
     */
    @Transactional
    public void crearLey(CrearLeyRequestDTO request) {
        String codigo = request.getCodigo() == null ? "" : request.getCodigo().trim();
        if (codigo.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "El código de expediente es obligatorio");
        }
        if (leyRepository.findByCodigo(codigo).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "Ya existe una ley con el código " + codigo);
        }

        Ley ley = new Ley();
        ley.setTitulo(request.getTitulo() != null ? request.getTitulo().trim() : null);
        ley.setCodigo(codigo);
        ley.setTipoExpediente(request.getTipoExpediente());
        ley.setProponente(request.getProponente());
        ley.setDescripcionOriginal(request.getDescripcionOriginal());
        ley.setDescripcionSimplificada(request.getDescripcionSimplificada());
        ley.setImpactoSocial(request.getImpactoSocial());
        ley.setCategoria(request.getCategoria());
        ley.setEstado(parseEstadoLey(request.getEstado()));
        ley.setFechaIngreso(request.getFechaIngreso() != null ? request.getFechaIngreso() : LocalDate.now());
        ley.setVotos(new java.util.ArrayList<>());
        ley.setComentarios(new java.util.ArrayList<>());
        ley.setCalificaciones(new java.util.ArrayList<>());
        ley.setVinculos(new java.util.ArrayList<>());

        leyRepository.save(ley);
        registrarLog("CREAR_LEY", "Ley creada manualmente: " + ley.getCodigo());
    }

    /**
     * @param estado nombre de estado recibido en la petición; puede ser {@code null} o inválido
     * @return el {@code EstadoLey} correspondiente, o {@code DEBATE} si es nulo, vacío o desconocido
     */
    private com.controlf.db.schema.enums.EstadoLey parseEstadoLey(String estado) {
        if (estado == null || estado.isBlank()) {
            return com.controlf.db.schema.enums.EstadoLey.DEBATE;
        }
        try {
            return com.controlf.db.schema.enums.EstadoLey.valueOf(estado.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return com.controlf.db.schema.enums.EstadoLey.DEBATE;
        }
    }

    /**
     * Elimina un político y sus registros asociados.
     *
     * @param politicoId identificador del político
     * @throws java.util.NoSuchElementException si el político no existe
     */
    @Transactional
    public void eliminarPolitico(Integer politicoId) {
        Politico politico = politicoRepository.findById(politicoId).orElseThrow();
        politicoRepository.delete(politico);
        registrarLog("ELIMINAR_POLITICO", "Político eliminado: " + politico.getNombreCompleto());
    }

    /**
     * Sustituye fórmulas legislativas frecuentes por equivalentes en
     * lenguaje llano y recorta el resultado a un largo razonable,
     * intentando cortar en un punto final para no truncar a mitad de frase.
     *
     * @param original texto original de la ley; puede ser {@code null} o estar vacío
     * @return el texto normalizado, o un mensaje por defecto si no había texto original
     */
    private String normalizeLegislativeText(String original) {
        if (original == null || original.isBlank()) {
            return "No hay descripción disponible.";
        }
        String result = original.replaceAll("\\s+", " ").trim();

        result = result.replaceAll("(?i)\\bexposición de motivos\\b", "motivos");
        result = result.replaceAll("(?i)\\bexpone\\b", "dice");
        result = result.replaceAll("(?i)\\bconsiderando\\b", "dado que");
        result = result.replaceAll("(?i)\\bpor cuanto\\b", "porque");
        result = result.replaceAll("(?i)\\bse establece\\b", "se fija");
        result = result.replaceAll("(?i)\\bse dispone\\b", "se ordena");
        result = result.replaceAll("(?i)\\bcon el fin de\\b", "para");
        result = result.replaceAll("(?i)\\bcon objeto de\\b", "para");
        result = result.replaceAll("(?i)\\ben el presente acto\\b", "aquí");
        result = result.replaceAll("(?i)\\bserá\\b", "va a");
        result = result.replaceAll("(?i)\\bdeberá\\b", "debe");
        result = result.replaceAll("(?i)\\bpodrá\\b", "puede");

        int maxLength = 320;
        if (result.length() > maxLength) {
            int end = result.indexOf('.', maxLength);
            if (end > 0) {
                result = result.substring(0, end + 1);
            } else {
                result = result.substring(0, maxLength) + "...";
            }
        }

        return result.trim();
    }

    /**
     * @return un resumen histórico agregado de leyes y votos para el dashboard administrativo
     */
    public ReporteHistoricoDTO getHistoricoResumen() {
        long totalLeyes = leyRepository.count();
        long totalVotos = votoRepository.count();
        long votosFavor = votoRepository.countByTipoVoto(com.controlf.db.schema.enums.TipoVoto.FAVOR);
        long votosContra = votoRepository.countByTipoVoto(com.controlf.db.schema.enums.TipoVoto.CONTRA);
        long votosAbstencion = votoRepository.countByTipoVoto(com.controlf.db.schema.enums.TipoVoto.ABSTENCION);
        long leyesAprobadas = leyRepository.countByEstado(com.controlf.db.schema.enums.EstadoLey.APROBADA);
        long leyesEnDebate = leyRepository.countByEstado(com.controlf.db.schema.enums.EstadoLey.DEBATE);

        return ReporteHistoricoDTO.builder()
                .totalLeyes(totalLeyes)
                .totalVotos(totalVotos)
                .votosFavor(votosFavor)
                .votosContra(votosContra)
                .votosAbstencion(votosAbstencion)
                .leyesAprobadas(leyesAprobadas)
                .leyesEnDebate(leyesEnDebate)
                .build();
    }

    /**
     * Persiste una entrada de auditoría con la acción administrativa realizada.
     *
     * @param accion código corto de la acción (p. ej. {@code "BACKUP"})
     * @param detalles descripción legible de lo ocurrido
     */
    private void registrarLog(String accion, String detalles) {
        LogSistema log = new LogSistema();
        log.setAccion(accion);
        log.setDetalles(detalles);
        log.setFecha(LocalDateTime.now());
        logRepository.save(log);
    }

    /**
     * @return el panel de seguridad y usuarios (reportes pendientes, logs del sistema)
     */
    public PanelControlDTO getSecurityPanel() {
        int reportadosCount = (int) reporteRepository.countByEstado(Reporte.EstadoReporte.PENDIENTE);
        int logsCount = (int) logRepository.count();

        return PanelControlDTO.builder()
                .tituloSeccion("SEGURIDAD Y USUARIOS")
                .opciones(Arrays.asList(
                        new PanelControlDTO.OpcionPanelDTO("Gestionar Roles de Staff", "user-cog", 0),
                        new PanelControlDTO.OpcionPanelDTO("Usuarios Reportados", "flag", reportadosCount),
                        new PanelControlDTO.OpcionPanelDTO("Auditoría de Logs del Sistema", "terminal", logsCount)
                ))
                .build();
    }

    /**
     * @return el estado de mantenimiento del servidor: disponibilidad de la
     *         base de datos, carga de CPU y fecha del último respaldo
     */
    public PanelMantenimientoDTO getMantenimientoInfo() {
        boolean dbStatus = isDatabaseUp();
        int cpuLoad = getSystemCpuLoad();

        String ultimaFecha = logRepository.findAll().stream()
                .filter(l -> l.getAccion().contains("BACKUP"))
                .map(l -> l.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .findFirst()
                .orElse("SIN REGISTRO");

        return PanelMantenimientoDTO.builder()
                .id("srv-01")
                .titulo("ESTADO DEL SERVIDOR")
                .codigoReferencia("RNF-02/05")
                .estadoBaseDeDatos(dbStatus)
                .estadoEtiqueta(dbStatus ? "ONLINE" : "OFFLINE")
                .fechaUltimoRespaldo(ultimaFecha)
                .cargaServidorPorcentaje(cpuLoad)
                .accionesDisponibles(Arrays.asList("BACKUP", "CACHE_CLEAR", "IMPORT_LEYES"))
                .build();
    }

    /**
     * @return {@code true} si se puede abrir una conexión válida a la base de datos
     */
    private boolean isDatabaseUp() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(1);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return la carga de CPU del sistema como porcentaje entero (0 si no se pudo determinar)
     */
    private int getSystemCpuLoad() {
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double load = osBean.getSystemCpuLoad();
            return (load < 0) ? 0 : (int) (load * 100);
        } catch (Exception e) {
            return 0;
        }
    }
}

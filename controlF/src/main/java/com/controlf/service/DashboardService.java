package com.controlf.service;

import com.controlf.db.repository.*;
import com.controlf.db.schema.Ley;
import com.controlf.db.schema.Voto;
import com.controlf.db.schema.VinculoPromesaLey;
import com.controlf.db.schema.enums.EstadoLey;
import com.controlf.db.schema.enums.TipoVoto;
import com.controlf.dto.DashboardStatsDTO;
import com.controlf.dto.MetricaItemDTO;
import com.controlf.dto.MetricasInteractivasDTO;
import com.controlf.dto.RecentActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Estadísticas agregadas, métricas interactivas filtrables y
 * exportaciones CSV para el dashboard público y el panel administrativo.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PoliticoRepository politicoRepository;
    private final LeyRepository leyRepository;
    private final ComentarioRepository comentarioRepository;
    private final VinculoPromesaLeyRepository vinculoRepository;
    private final VotoRepository votoRepository;
    private final CalificacionRepository calificacionRepository;

    /**
     * @return las estadísticas generales del dashboard (totales y
     *         actividad reciente), usando la misma escala de coherencia
     *         que el resto del sistema para que el promedio global
     *         coincida con el de la sección de Métricas
     */
    public DashboardStatsDTO getStats() {
        Double avgCoherencia = vinculoRepository.findAll().stream()
                .mapToDouble(this::scoreCoherencia)
                .average()
                .orElse(0.0);

        var actividad = comentarioRepository.findAll().stream()
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .limit(5)
                .map(c -> RecentActivityDTO.builder()
                        .tipo("COMENTARIO")
                        .usuario(c.getUsuario().getNombre())
                        .detalle("Comentó en: " + c.getTexto())
                        .fecha(c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")))
                        .build())
                .collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalPoliticos(politicoRepository.count())
                .totalLeyes(leyRepository.count())
                .promedioCoherenciaGlobal(Math.round(avgCoherencia * 10.0) / 10.0)
                .totalComentarios(comentarioRepository.count())
                .actividadReciente(actividad)
                .build();
    }

    /**
     * Calcula métricas de cumplimiento interactivas aplicando filtros de
     * categoría, estado y rango de fechas sobre datos reales: leyes por
     * estado/categoría, votos por tipo, coherencia por categoría y una
     * serie temporal mensual de votos.
     *
     * @param categoria filtro opcional por categoría de ley
     * @param estado filtro opcional por estado de ley (nombre del enum, o {@code "TODOS"})
     * @param desde fecha inicial opcional del rango, en formato ISO ({@code yyyy-MM-dd})
     * @param hasta fecha final opcional del rango, en formato ISO ({@code yyyy-MM-dd})
     * @return las métricas agregadas resultantes de aplicar los filtros
     */
    public MetricasInteractivasDTO getMetricasInteractivas(String categoria, String estado, String desde, String hasta) {
        String categoriaFiltro = (categoria != null && !categoria.isBlank()) ? categoria.trim() : null;
        EstadoLey estadoFiltro = parseEstado(estado);
        LocalDate desdeFiltro = parseFecha(desde);
        LocalDate hastaFiltro = parseFecha(hasta);

        List<Ley> leyes = leyRepository.findAll().stream()
                .filter(l -> categoriaFiltro == null || categoriaFiltro.equalsIgnoreCase(l.getCategoria()))
                .filter(l -> estadoFiltro == null || l.getEstado() == estadoFiltro)
                .collect(Collectors.toList());
        Set<Integer> leyIds = leyes.stream().map(Ley::getId).collect(Collectors.toSet());

        Map<String, Long> porEstado = new LinkedHashMap<>();
        for (EstadoLey e : EstadoLey.values()) {
            porEstado.put(e.name(), 0L);
        }
        for (Ley l : leyes) {
            if (l.getEstado() != null) {
                porEstado.merge(l.getEstado().name(), 1L, Long::sum);
            }
        }

        Map<String, Long> porCategoria = new LinkedHashMap<>();
        for (Ley l : leyes) {
            String c = (l.getCategoria() == null || l.getCategoria().isBlank()) ? "SIN CATEGORÍA" : l.getCategoria();
            porCategoria.merge(c, 1L, Long::sum);
        }

        List<Voto> votos = votoRepository.findAll().stream()
                .filter(v -> v.getLey() != null && leyIds.contains(v.getLey().getId()))
                .filter(v -> enRango(v.getFechaVoto(), desdeFiltro, hastaFiltro))
                .collect(Collectors.toList());

        long favor = votos.stream().filter(v -> v.getTipoVoto() == TipoVoto.FAVOR).count();
        long contra = votos.stream().filter(v -> v.getTipoVoto() == TipoVoto.CONTRA).count();
        long abstencion = votos.stream().filter(v -> v.getTipoVoto() == TipoVoto.ABSTENCION).count();

        Map<String, Long> serie = new java.util.TreeMap<>();
        for (Voto v : votos) {
            if (v.getFechaVoto() != null) {
                String periodo = v.getFechaVoto().toLocalDate().toString().substring(0, 7);
                serie.merge(periodo, 1L, Long::sum);
            }
        }
        serie = rellenarMesesContinuos(serie);

        Map<String, long[]> acumCoherencia = new LinkedHashMap<>();
        double sumaGlobal = 0.0;
        long conteoGlobal = 0;
        for (VinculoPromesaLey v : vinculoRepository.findAll()) {
            if (v.getLey() == null || !leyIds.contains(v.getLey().getId())) {
                continue;
            }
            long score = scoreCoherencia(v);
            String c = (v.getLey().getCategoria() == null || v.getLey().getCategoria().isBlank())
                    ? "SIN CATEGORÍA" : v.getLey().getCategoria();
            long[] slot = acumCoherencia.computeIfAbsent(c, k -> new long[2]);
            slot[0] += score;
            slot[1] += 1;
            sumaGlobal += score;
            conteoGlobal += 1;
        }

        List<MetricaItemDTO> coherenciaPorCategoria = new ArrayList<>();
        for (Map.Entry<String, long[]> e : acumCoherencia.entrySet()) {
            double promedio = e.getValue()[1] > 0 ? Math.round((double) e.getValue()[0] / e.getValue()[1] * 10.0) / 10.0 : 0.0;
            coherenciaPorCategoria.add(new MetricaItemDTO(e.getKey(), promedio));
        }

        double promedioGlobal = conteoGlobal > 0 ? Math.round(sumaGlobal / conteoGlobal * 10.0) / 10.0 : 0.0;

        return MetricasInteractivasDTO.builder()
                .categoriaFiltro(categoriaFiltro)
                .estadoFiltro(estadoFiltro != null ? estadoFiltro.name() : null)
                .desde(desdeFiltro != null ? desdeFiltro.toString() : null)
                .hasta(hastaFiltro != null ? hastaFiltro.toString() : null)
                .totalLeyes(leyes.size())
                .totalVotos(votos.size())
                .promedioCoherenciaGlobal(promedioGlobal)
                .leyesPorEstado(toItems(porEstado))
                .leyesPorCategoria(toItems(porCategoria))
                .votosPorTipo(List.of(
                        new MetricaItemDTO("FAVOR", favor),
                        new MetricaItemDTO("CONTRA", contra),
                        new MetricaItemDTO("ABSTENCION", abstencion)))
                .coherenciaPorCategoria(coherenciaPorCategoria)
                .serieVotosPorMes(toItems(serie))
                .build();
    }

    /**
     * Traduce un nivel de coherencia a un puntaje numérico en la misma
     * escala usada en todo el sistema (0-100), para que los promedios
     * calculados aquí sean comparables con los de otras vistas.
     *
     * @param v vínculo de coherencia a puntuar
     * @return 100 para {@code CUMPLE}, 50 para {@code AMBIGUO}, 0 para {@code INCUMPLE} o nivel nulo
     */
    private long scoreCoherencia(VinculoPromesaLey v) {
        if (v.getNivelCoherencia() == null) return 0;
        return switch (v.getNivelCoherencia()) {
            case CUMPLE -> 100;
            case AMBIGUO -> 50;
            case INCUMPLE -> 0;
        };
    }

    /**
     * Completa con ceros los meses intermedios sin votos entre el primer y
     * el último periodo presentes, para que la serie temporal sea continua
     * y un mes con muchos votos no quede pegado visualmente a otro mes lejano.
     *
     * @param serie serie de conteos por periodo ({@code yyyy-MM}), ordenada cronológicamente
     * @return la misma serie si tiene menos de dos periodos; en caso
     *         contrario, una serie continua con los huecos rellenados en 0
     */
    private Map<String, Long> rellenarMesesContinuos(Map<String, Long> serie) {
        if (serie.size() < 2) {
            return serie;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth primero = YearMonth.parse(((java.util.TreeMap<String, Long>) serie).firstKey(), fmt);
        YearMonth ultimo = YearMonth.parse(((java.util.TreeMap<String, Long>) serie).lastKey(), fmt);

        Map<String, Long> continua = new LinkedHashMap<>();
        for (YearMonth ym = primero; !ym.isAfter(ultimo); ym = ym.plusMonths(1)) {
            String clave = ym.format(fmt);
            continua.put(clave, serie.getOrDefault(clave, 0L));
        }
        return continua;
    }

    /**
     * @param mapa pares clave-valor a convertir
     * @return el mismo contenido como lista de {@link MetricaItemDTO}, preservando el orden de iteración
     */
    private List<MetricaItemDTO> toItems(Map<String, Long> mapa) {
        List<MetricaItemDTO> items = new ArrayList<>();
        for (Map.Entry<String, Long> e : mapa.entrySet()) {
            items.add(new MetricaItemDTO(e.getKey(), e.getValue()));
        }
        return items;
    }

    /**
     * @param estado nombre del estado a parsear; puede ser {@code null}, vacío o {@code "TODOS"}
     * @return el {@link EstadoLey} correspondiente, o {@code null} si no se debe filtrar o el valor es inválido
     */
    private EstadoLey parseEstado(String estado) {
        if (estado == null || estado.isBlank() || estado.equalsIgnoreCase("TODOS")) {
            return null;
        }
        try {
            return EstadoLey.valueOf(estado.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * @param fecha fecha en formato ISO a parsear; puede ser {@code null} o vacía
     * @return la fecha parseada, o {@code null} si es nula, vacía o inválida
     */
    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param fechaVoto fecha y hora del voto; puede ser {@code null}
     * @param desde límite inferior inclusivo del rango; {@code null} para no acotar
     * @param hasta límite superior inclusivo del rango; {@code null} para no acotar
     * @return {@code true} si el voto cae dentro del rango. Un voto sin
     *         fecha solo se incluye cuando no se está filtrando por rango.
     */
    private boolean enRango(LocalDateTime fechaVoto, LocalDate desde, LocalDate hasta) {
        if (fechaVoto == null) {
            return desde == null && hasta == null;
        }
        LocalDate fecha = fechaVoto.toLocalDate();
        if (desde != null && fecha.isBefore(desde)) {
            return false;
        }
        return hasta == null || !fecha.isAfter(hasta);
    }

    /**
     * @return un CSV con el detalle por político: coherencia, reputación y desglose de votos
     */
    public String exportPoliticosCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Nombre,Partido,Cargo,Region,Coherencia %,Reputacion (1-5),Total Calificaciones,Total Votos,Favor,Contra,Abstencion\n");
        for (com.controlf.db.schema.Politico p : politicoRepository.findAll()) {
            Double coherencia = vinculoRepository.findAverageCoherenciaByPoliticoId(p.getId());
            Double reputacion = calificacionRepository.findAveragePuntajeByPoliticoId(p.getId());
            long totalCalificaciones = calificacionRepository.countByPoliticoId(p.getId());
            List<Voto> votos = votoRepository.findByPoliticoId(p.getId());
            long favor = votos.stream().filter(v -> v.getTipoVoto() == TipoVoto.FAVOR).count();
            long contra = votos.stream().filter(v -> v.getTipoVoto() == TipoVoto.CONTRA).count();
            long abstencion = votos.stream().filter(v -> v.getTipoVoto() == TipoVoto.ABSTENCION).count();

            csv.append(p.getId()).append(',')
                    .append(escape(p.getNombreCompleto())).append(',')
                    .append(escape(p.getPartidoPolitico())).append(',')
                    .append(escape(p.getCargoActual())).append(',')
                    .append(escape(p.getRegion())).append(',')
                    .append(coherencia != null ? Math.round(coherencia * 10.0) / 10.0 : 0.0).append(',')
                    .append(reputacion != null ? Math.round(reputacion * 10.0) / 10.0 : 0.0).append(',')
                    .append(totalCalificaciones).append(',')
                    .append(votos.size()).append(',')
                    .append(favor).append(',')
                    .append(contra).append(',')
                    .append(abstencion).append('\n');
        }
        return csv.toString();
    }

    /**
     * @return un CSV con el detalle por ley: estado, categoría y resultado de la votación
     */
    public String exportLeyesCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Codigo,Titulo,Categoria,Estado,Proponente,Favor,Contra,Abstencion,Total Votos\n");
        for (Ley ley : leyRepository.findAll()) {
            long favor = votoRepository.countByLeyIdAndTipoVoto(ley.getId(), TipoVoto.FAVOR);
            long contra = votoRepository.countByLeyIdAndTipoVoto(ley.getId(), TipoVoto.CONTRA);
            long abstencion = votoRepository.countByLeyIdAndTipoVoto(ley.getId(), TipoVoto.ABSTENCION);

            csv.append(ley.getId()).append(',')
                    .append(escape(ley.getCodigo())).append(',')
                    .append(escape(ley.getTitulo())).append(',')
                    .append(escape(ley.getCategoria())).append(',')
                    .append(escape(ley.getEstado() != null ? ley.getEstado().name() : "SIN ESTADO")).append(',')
                    .append(escape(ley.getProponente())).append(',')
                    .append(favor).append(',')
                    .append(contra).append(',')
                    .append(abstencion).append(',')
                    .append(favor + contra + abstencion).append('\n');
        }
        return csv.toString();
    }

    /**
     * Aplica el escape mínimo requerido por el formato CSV (comillas
     * alrededor del valor si contiene comas, comillas o saltos de línea).
     *
     * @param value valor a escapar; puede ser {@code null}
     * @return el valor listo para insertarse en una celda CSV
     */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    /**
     * @return las estadísticas generales del dashboard como CSV
     */
    public String exportStatsCsv() {
        DashboardStatsDTO stats = getStats();
        StringBuilder csv = new StringBuilder();
        csv.append("Métrica,Valor\n");
        csv.append("Total Políticos,").append(stats.getTotalPoliticos()).append("\n");
        csv.append("Total Leyes,").append(stats.getTotalLeyes()).append("\n");
        csv.append("Promedio Coherencia Global,").append(stats.getPromedioCoherenciaGlobal()).append("\n");
        csv.append("Total Comentarios,").append(stats.getTotalComentarios()).append("\n");
        csv.append("Actividad Reciente,\n");
        for (RecentActivityDTO activity : stats.getActividadReciente()) {
            csv.append('"').append(activity.getTipo()).append(" - ")
                    .append(activity.getUsuario()).append(" - ")
                    .append(activity.getDetalle()).append('"')
                    .append(',').append(activity.getFecha()).append("\n");
        }
        return csv.toString();
    }
}

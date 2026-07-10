package com.controlf.service;

import com.controlf.db.repository.*;
import com.controlf.dto.DashboardStatsDTO;
import com.controlf.dto.RecentActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PoliticoRepository politicoRepository;
    private final LeyRepository leyRepository;
    private final ComentarioRepository comentarioRepository;
    private final VinculoPromesaLeyRepository vinculoRepository;

    public DashboardStatsDTO getStats() {
        Double avgCoherencia = vinculoRepository.findAll().stream()
                .mapToDouble(v -> v.getNivelCoherencia().name().equals("CUMPLE") ? 100.0 : 0.0) // Simplificado
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

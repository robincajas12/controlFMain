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
}

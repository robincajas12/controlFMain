package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalPoliticos;
    private long totalLeyes;
    private double promedioCoherenciaGlobal;
    private long totalComentarios;
    private List<RecentActivityDTO> actividadReciente;
}

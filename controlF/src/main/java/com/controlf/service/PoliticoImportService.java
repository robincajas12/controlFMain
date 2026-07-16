package com.controlf.service;

import com.controlf.db.repository.PoliticoRepository;
import com.controlf.db.schema.Politico;
import com.controlf.dto.AssemblyMemberDTO;
import com.controlf.dto.PoliticoImportResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Importa miembros de la asamblea (obtenidos vía {@link AssemblyImportService})
 * como registros {@code Politico}, evitando duplicados por nombre.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PoliticoImportService {

    private final PoliticoRepository politicoRepository;
    private final AssemblyImportService assemblyImportService;

    /**
     * Importa todos los miembros de la asamblea disponibles en la fuente externa.
     *
     * @return el resultado con totales de encontrados, importados y duplicados
     */
    public PoliticoImportResultDTO importAll() {
        List<AssemblyMemberDTO> members = assemblyImportService.getAssemblyMembers();
        if (members == null || members.isEmpty()) {
            return new PoliticoImportResultDTO(0, 0, 0);
        }

        int found = members.size();
        int imported = 0;
        int duplicates = 0;

        for (AssemblyMemberDTO m : members) {
            boolean isDuplicate = procesarGuardadoPolitico(m);
            if (isDuplicate) {
                duplicates++;
            } else {
                imported++;
            }
        }

        return new PoliticoImportResultDTO(found, imported, duplicates);
    }

    /**
     * Importa únicamente los miembros de la asamblea cuyo identificador
     * externo está incluido en {@code selectedIds}.
     *
     * @param selectedIds identificadores externos a importar
     * @return el resultado con totales de encontrados, importados y duplicados
     */
    public PoliticoImportResultDTO importSelected(List<Long> selectedIds) {
        List<AssemblyMemberDTO> allMembers = assemblyImportService.getAssemblyMembers();
        if (allMembers == null || allMembers.isEmpty() || selectedIds == null || selectedIds.isEmpty()) {
            return new PoliticoImportResultDTO(0, 0, 0);
        }

        int found = selectedIds.size();
        int imported = 0;
        int duplicates = 0;

        for (AssemblyMemberDTO m : allMembers) {
            if (selectedIds.contains(m.getId())) {
                boolean isDuplicate = procesarGuardadoPolitico(m);
                if (isDuplicate) {
                    duplicates++;
                } else {
                    imported++;
                }
            }
        }

        return new PoliticoImportResultDTO(found, imported, duplicates);
    }

    /**
     * Persiste un miembro de la asamblea como {@code Politico}, salvo que
     * ya exista uno con el mismo nombre completo (comparación insensible a
     * mayúsculas/minúsculas) o que el nombre resultante quede vacío.
     *
     * @param m miembro de la asamblea a procesar
     * @return {@code true} si se omitió por ser un nombre vacío o duplicado, {@code false} si se guardó
     */
    private boolean procesarGuardadoPolitico(AssemblyMemberDTO m) {
        String nombre = ((m.getFirstName() != null ? m.getFirstName() : "") + " " +
                (m.getLastname() != null ? m.getLastname() : "")).trim().toUpperCase();

        if (nombre.isEmpty()) return true;

        if (politicoRepository.findByNombreCompletoContainingIgnoreCase(nombre).isPresent()) {
            log.debug("Político duplicado omitido: {}", nombre);
            return true;
        }

        Politico politico = new Politico();
        politico.setNombreCompleto(nombre);
        politico.setPartidoPolitico("INDEPENDIENTE");
        politico.setCargoActual("Asambleísta Nacional");
        politico.setRegion(m.getTerritorial());
        politico.setComision("Legislativo");
        politico.setEstaActivo(true);
        politico.setPatrimonioDeclarado(BigDecimal.ZERO);
        politico.setAntecedentes(null);
        politico.setFotoUrl(null);

        politico.setPromesas(List.of());
        politico.setVotos(List.of());
        politico.setComentarios(List.of());
        politico.setCalificaciones(List.of());

        politicoRepository.save(politico);
        log.info("Político importado exitosamente: {}", nombre);
        return false;
    }
}

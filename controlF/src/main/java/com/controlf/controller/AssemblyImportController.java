package com.controlf.controller;

import com.controlf.dto.AssemblyMemberDTO;
import com.controlf.dto.ImportLeyesRequestDTO;
import com.controlf.dto.ImportResultDTO;
import com.controlf.dto.VotingDTO;
import com.controlf.service.AssemblyImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Importa datos de miembros de la asamblea y sus votaciones desde la
 * fuente externa hacia el modelo interno de políticos y leyes.
 */
@RestController
@RequestMapping({"/admin", "/api/admin"})
@RequiredArgsConstructor
@CrossOrigin("*")
public class AssemblyImportController {

    private final AssemblyImportService assemblyImportService;

    /**
     * @return los miembros de la asamblea disponibles en la fuente externa
     */
    @GetMapping("/assembly-members")
    public ResponseEntity<List<AssemblyMemberDTO>> getAssemblyMembers() {
        return ResponseEntity.ok(assemblyImportService.getAssemblyMembers());
    }

    /**
     * @param id identificador externo del miembro de la asamblea
     * @return las votaciones disponibles para ese miembro
     * @throws RuntimeException si la fuente externa falla al resolver las votaciones
     */
 @GetMapping("/assembly-members/{id}/votings")
public ResponseEntity<List<VotingDTO>> getVotings(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(assemblyImportService.getVotings(id));
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
    }
}

    /**
     * Importa únicamente las votaciones seleccionadas para un miembro dado.
     *
     * @param memberId identificador externo del miembro de la asamblea
     * @param selectedIds identificadores externos de las votaciones a importar
     * @return el resultado de la importación
     */
    @PostMapping("/import-votings/{memberId}/selected")
    public ResponseEntity<ImportResultDTO> importSelectedVotings(
            @PathVariable Long memberId,
            @RequestBody List<Long> selectedIds) {
        return ResponseEntity.ok(assemblyImportService.importSelectedVotings(memberId, selectedIds));
    }

    /**
     * Importa las leyes asociadas a un conjunto de políticos ya importados.
     *
     * @param request identificadores de los políticos cuyas leyes se importarán
     * @return el resultado de la importación
     */
    @PostMapping("/import-leyes")
    public ResponseEntity<ImportResultDTO> importLeyesForPoliticos(@Valid @RequestBody ImportLeyesRequestDTO request) {
        return ResponseEntity.ok(assemblyImportService.importLeyesForPoliticos(request.getPoliticoIds()));
    }

    /**
     * Importa todas las votaciones de un miembro de la asamblea.
     *
     * @param id identificador externo del miembro de la asamblea
     * @return el resultado de la importación
     */
    @PostMapping("/import-votings/{id}")
    public ResponseEntity<ImportResultDTO> importVotings(@PathVariable Long id) {
        return ResponseEntity.ok(assemblyImportService.importVotings(id));
    }

    /**
     * Traduce cualquier fallo no controlado de la importación a una
     * respuesta 404 con el mensaje de la causa original.
     *
     * @param ex la excepción capturada
     * @return una respuesta 404 con el mensaje de error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

package com.controlf.controller;

import com.controlf.dto.PoliticoImportResultDTO;
import com.controlf.service.PoliticoImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Importa políticos desde la fuente externa hacia el modelo interno.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PoliticoImportController {

    private final PoliticoImportService politicoImportService;

    /**
     * Importa todos los políticos disponibles en la fuente externa.
     *
     * @return el resultado de la importación
     */
    @PostMapping("/import-politicos")
    public ResponseEntity<PoliticoImportResultDTO> importAll() {
        return ResponseEntity.ok(politicoImportService.importAll());
    }

    /**
     * Importa únicamente los políticos seleccionados.
     *
     * @param selectedIds identificadores externos de los políticos a importar
     * @return el resultado de la importación
     */
    @PostMapping("/import-politicos/selected")
    public ResponseEntity<PoliticoImportResultDTO> importSelected(@RequestBody List<Long> selectedIds) {
        return ResponseEntity.ok(politicoImportService.importSelected(selectedIds));
    }
}
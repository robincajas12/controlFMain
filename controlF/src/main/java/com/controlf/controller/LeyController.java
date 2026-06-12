package com.controlf.controller;

import com.controlf.dto.*;
import com.controlf.service.LeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leyes")
@RequiredArgsConstructor
public class LeyController {

    private final LeyService leyService;

    @GetMapping("/filtros")
    public com.controlf.dto.FiltrosLeyDTO getFiltros() {
        return leyService.getFiltros();
    }

    @GetMapping
    public com.controlf.dto.GrillaLeyesDTO getLeyes(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado) {
        return leyService.getLeyesFiltradas(pagina, size, termino, categoria, estado);
    }

    @GetMapping("/{id}/perfil")
    public PerfilLeyDTO getPerfilLey(@PathVariable Integer id) {
        return leyService.getFullPerfilLey(id);
    }

    @GetMapping("/{id}")
    public ContenidoLeyDTO getLey(@PathVariable Integer id) {
        return leyService.getContenidoLey(id);
    }

    @GetMapping("/{id}/votacion")
    public ResultadoVotacionDTO getVotacion(@PathVariable Integer id) {
        return leyService.getResultadoVotacion(id);
    }

    @GetMapping("/{id}/debate")
    public DebateCiudadanoDTO getDebate(@PathVariable Integer id) {
        return leyService.getDebateCiudadano(id);
    }

    @GetMapping("/{id}/auditoria")
    public AuditoriaCoherenciaDTO getAuditoria(@PathVariable Integer id) {
        return leyService.getAuditoriaCoherencia(id);
    }

    @PostMapping("/{id}/comentarios")
    public void postComentario(@PathVariable Integer id, @RequestBody ComentarioRequestDTO request) {
        leyService.addComentario(id, request);
    }

    @PostMapping("/{id}/calificaciones")
    public void postCalificacion(@PathVariable Integer id, @RequestBody CalificacionRequestDTO request) {
        leyService.addCalificacion(id, request);
    }
}

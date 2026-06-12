package com.controlf.controller;

import com.controlf.dto.PanelControlDTO;
import com.controlf.dto.PanelMantenimientoDTO;
import com.controlf.dto.VinculoRequestDTO;
import com.controlf.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/motor/data")
    public com.controlf.dto.MotorCoherenciaDataDTO getMotorData() {
        return adminService.getMotorData();
    }

    @GetMapping("/politicos/{id}/promesas")
    public java.util.List<com.controlf.dto.SimpleItemDTO> getPromesas(@PathVariable Integer id) {
        return adminService.getPromesasByPolitico(id);
    }

    @GetMapping("/panel")
    public PanelControlDTO getPanel() {
        return adminService.getSecurityPanel();
    }

    @GetMapping("/mantenimiento")
    public PanelMantenimientoDTO getMantenimiento() {
        return adminService.getMantenimientoInfo();
    }

    @PostMapping("/vinculos")
    public void postVinculo(@RequestBody VinculoRequestDTO request) {
        adminService.crearVinculoCoherencia(request);
    }

    @PostMapping("/mantenimiento/respaldo")
    public void postRespaldo() {
        adminService.ejecutarRespaldo(null); // ID de admin simplificado para el ejemplo
    }

    @PostMapping("/mantenimiento/limpiar-cache")
    public void postLimpiarCache() {
        adminService.limpiarCache();
    }

    @PostMapping("/importar-leyes")
    public void postImportarLeyes() {
        adminService.importarLeyes();
    }
}

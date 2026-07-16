package com.controlf.controller;

import com.controlf.dto.CrearPoliticoRequestDTO;
import com.controlf.dto.CrearPromesaRequestDTO;
import com.controlf.dto.PanelControlDTO;
import com.controlf.dto.PanelMantenimientoDTO;
import com.controlf.dto.VinculoRequestDTO;
import com.controlf.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Operaciones administrativas restringidas al rol {@code ADMIN} (ver
 * {@link com.controlf.auth.SecurityConfig}): gestión de contenido (leyes,
 * políticos, promesas, vínculos de coherencia), mantenimiento del sistema
 * y endpoints de reportería.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final com.controlf.service.DataSeederService dataSeederService;

    /**
     * Puebla la base de datos con datos base de referencia/demostración.
     *
     * @return un mensaje de confirmación
     */
    @PostMapping("/seed")
    public String seedData() {
        dataSeederService.seed();
        return "Database seeded successfully";
    }

    /**
     * @return los datos que alimentan el panel administrativo del motor de coherencia
     */
    @GetMapping("/motor/data")
    public com.controlf.dto.MotorCoherenciaDataDTO getMotorData() {
        return adminService.getMotorData();
    }

    /**
     * @param id identificador del político
     * @return las promesas de campaña registradas para ese político
     */
    @GetMapping("/politicos/{id}/promesas")
    public java.util.List<com.controlf.dto.SimpleItemDTO> getPromesas(@PathVariable Integer id) {
        return adminService.getPromesasByPolitico(id);
    }

    /**
     * Registra una nueva promesa de campaña.
     *
     * @param request datos de la promesa a persistir
     */
    @PostMapping("/promesas")
    public void crearPromesa(@RequestBody CrearPromesaRequestDTO request) {
        adminService.crearPromesa(request);
    }

    /**
     * Registra un nuevo político.
     *
     * @param request datos del político a persistir
     */
    @PostMapping("/politicos")
    public void crearPolitico(@RequestBody CrearPoliticoRequestDTO request) {
        adminService.crearPolitico(request);
    }

    /**
     * Registra una nueva ley.
     *
     * @param request datos de la ley a persistir
     */
    @PostMapping("/leyes")
    public void crearLey(@Valid @RequestBody com.controlf.dto.CrearLeyRequestDTO request) {
        adminService.crearLey(request);
    }

    /**
     * Elimina un político junto con sus registros asociados.
     *
     * @param id identificador del político
     */
    @DeleteMapping("/politicos/{id}")
    public void eliminarPolitico(@PathVariable Integer id) {
        adminService.eliminarPolitico(id);
    }

    /**
     * @return el resumen del panel de control de seguridad/auditoría
     */
    @GetMapping("/panel")
    public PanelControlDTO getPanel() {
        return adminService.getSecurityPanel();
    }

    /**
     * @return el estado de mantenimiento del sistema (caché, respaldos, etc.)
     */
    @GetMapping("/mantenimiento")
    public PanelMantenimientoDTO getMantenimiento() {
        return adminService.getMantenimientoInfo();
    }

    /**
     * Crea un vínculo de coherencia entre una promesa y un registro
     * relacionado (por ejemplo, una ley o un voto) usado por el motor de
     * coherencia.
     *
     * @param request el vínculo a crear
     */
    @PostMapping("/vinculos")
    public void postVinculo(@Valid @RequestBody VinculoRequestDTO request) {
        adminService.crearVinculoCoherencia(request);
    }

    /**
     * Ejecuta un respaldo de datos.
     */
    @PostMapping("/mantenimiento/respaldo")
    public void postRespaldo() {
        adminService.ejecutarRespaldo(null);
    }

    /**
     * Limpia las cachés del servidor.
     */
    @PostMapping("/mantenimiento/limpiar-cache")
    public void postLimpiarCache() {
        adminService.limpiarCache();
    }

    /**
     * @return un resumen histórico de actividad para el dashboard administrativo
     */
    @GetMapping("/historico")
    public com.controlf.dto.ReporteHistoricoDTO getHistorico() {
        return adminService.getHistoricoResumen();
    }

    /**
     * Importa leyes desde la fuente externa configurada.
     */
    @PostMapping("/importar-leyes")
    public void postImportarLeyes() {
        adminService.importarLeyes();
    }

    /**
     * Normaliza los registros de leyes existentes (por ejemplo, limpieza de
     * categoría/estado).
     *
     * @return un resumen de los cambios aplicados
     */
    @PostMapping("/normalizar-leyes")
    public com.controlf.dto.LeyNormalizacionResultDTO postNormalizarLeyes() {
        return adminService.normalizarLeyes();
    }

    /**
     * @return las leyes elegibles para sincronización con la fuente externa
     */
    @GetMapping("/leyes/syncable")
    public java.util.List<com.controlf.dto.LeySyncItemDTO> getLeyesSyncables() {
        return adminService.listarLeyesParaSync();
    }
}

package com.controlf.controller;

import com.controlf.auth.JwtService;
import com.controlf.db.repository.UsuarioRepository;
import com.controlf.db.schema.Usuario;
import com.controlf.dto.AuthLoginRequestDTO;
import com.controlf.dto.AuthRegisterRequestDTO;
import com.controlf.dto.AuthResponseDTO;
import com.controlf.dto.AuthMeResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro, login y consulta de la sesión actual. El registro permite que
 * el propio usuario elija su rol (incluido ADMIN/VALIDADOR); es un
 * comportamiento pensado para entornos de desarrollo, no para producción.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Registra un nuevo usuario y devuelve un token de sesión listo para usar.
     *
     * @param request datos de registro (nombre, email, contraseña, rol opcional)
     * @return un {@link AuthResponseDTO} con el token emitido si el registro
     *         fue exitoso, o un 409 con el campo en conflicto (email o
     *         nombre ya registrados)
     */
    @PostMapping("/registro")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterRequestDTO request) {
        String nombre = request.getNombre().trim();

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("campo", "email", "mensaje", "Este correo ya está registrado"));
        }
        if (usuarioRepository.existsByNombreIgnoreCase(nombre)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("campo", "nombre", "mensaje", "Este nombre de usuario ya está en uso"));
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(resolveRol(request.getRol()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario.getEmail(), usuario.getRol().name());
        return ResponseEntity.ok(new AuthResponseDTO(token, Map.of("id", usuario.getId(), "email", usuario.getEmail(), "nombre", usuario.getNombre(), "rol", usuario.getRol().name())));
    }

    /**
     * Verifica disponibilidad de email y/o nombre de usuario, usada por el
     * formulario de registro para avisar antes de enviar si alguno ya está
     * en uso y sugerir alternativas. Operación de solo lectura.
     *
     * @param request mapa con las claves opcionales {@code "email"} y {@code "nombre"} a verificar
     * @return un mapa con {@code emailDisponible}, {@code nombreDisponible}
     *         y, si el nombre está ocupado, una lista de {@code sugerencias}
     */
    @PostMapping("/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();

        String email = request.get("email");
        if (email != null && !email.isBlank()) {
            result.put("emailDisponible", usuarioRepository.findByEmail(email.trim()).isEmpty());
        }

        String nombre = request.get("nombre");
        if (nombre != null && !nombre.isBlank()) {
            String base = nombre.trim();
            boolean disponible = !usuarioRepository.existsByNombreIgnoreCase(base);
            result.put("nombreDisponible", disponible);
            if (!disponible) {
                result.put("sugerencias", generarSugerenciasNombre(base));
            }
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Resuelve el rol solicitado en el registro a un valor válido de
     * {@link Usuario.Rol}, sin distinguir mayúsculas/minúsculas.
     *
     * @param rol nombre de rol solicitado; puede ser {@code null} o vacío
     * @return el rol resuelto, o {@link Usuario.Rol#CIUDADANO} si es nulo,
     *         vacío o no coincide con ningún rol conocido
     */
    private Usuario.Rol resolveRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return Usuario.Rol.CIUDADANO;
        }
        try {
            return Usuario.Rol.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Usuario.Rol.CIUDADANO;
        }
    }

    /**
     * Genera hasta cuatro variantes disponibles de un nombre de usuario ya
     * ocupado (sufijos numéricos, sin espacios, u otros sufijos), para
     * ofrecerlas como sugerencias en el formulario de registro.
     *
     * @param base nombre de usuario ya en uso
     * @return hasta cuatro nombres alternativos disponibles
     */
    private List<String> generarSugerenciasNombre(String base) {
        List<String> sugerencias = new ArrayList<>();
        String sinEspacios = base.replaceAll("\\s+", "");

        List<String> candidatos = new ArrayList<>();
        for (int i = 1; i <= 99; i++) {
            candidatos.add(base + " " + i);
        }
        candidatos.add(sinEspacios);
        candidatos.add(base + "_");
        candidatos.add(base + ".oficial");

        for (String candidato : candidatos) {
            if (sugerencias.size() >= 4) {
                break;
            }
            if (!candidato.equalsIgnoreCase(base)
                    && !usuarioRepository.existsByNombreIgnoreCase(candidato)
                    && !sugerencias.contains(candidato)) {
                sugerencias.add(candidato);
            }
        }

        return sugerencias;
    }

    /**
     * Autentica un usuario por email y contraseña y emite un token de sesión.
     *
     * @param request credenciales de acceso
     * @return un {@link AuthResponseDTO} con el token emitido, también
     *         devuelto en el header {@code Authorization}; 401 si las
     *         credenciales no son válidas
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.generateToken(request.getEmail(), authentication.getAuthorities().stream().findFirst().orElseThrow().getAuthority().replace("ROLE_", ""));
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body(new AuthResponseDTO(token, Map.of("email", request.getEmail(), "rol", authentication.getAuthorities().stream().findFirst().orElseThrow().getAuthority().replace("ROLE_", ""))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * @return los datos del usuario autenticado en la sesión actual, o 401
     *         si no hay una sesión válida
     */
    @GetMapping("/me")
    public ResponseEntity<AuthMeResponseDTO> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(new AuthMeResponseDTO(usuario.getId(), usuario.getEmail(), usuario.getNombre(), usuario.getRol().name()));
    }
}

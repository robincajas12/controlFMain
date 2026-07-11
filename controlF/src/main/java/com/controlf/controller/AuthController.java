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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/registro")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterRequestDTO request) {
        String nombre = request.getNombre().trim();

        // Se conserva la validación de correo ya existente y se devuelve el campo
        // en conflicto para poder mostrar el error junto al campo correspondiente.
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
     * Verificación de disponibilidad usada por el formulario de registro para avisar
     * antes de enviar si un correo o nombre ya está en uso y sugerir alternativas.
     * Es de solo lectura y se apoya en las consultas ya existentes del repositorio.
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

    private Usuario.Rol resolveRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return Usuario.Rol.CIUDADANO;
        }
        try {
            // Solo se aceptan los roles ya definidos en el sistema; cualquier otro valor
            // cae en CIUDADANO para no alterar la lógica de permisos existente.
            return Usuario.Rol.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Usuario.Rol.CIUDADANO;
        }
    }

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

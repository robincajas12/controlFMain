package com.controlf.auth;

import com.controlf.db.repository.ComentarioRepository;
import com.controlf.db.repository.PoliticoRepository;
import com.controlf.db.repository.UsuarioRepository;
import com.controlf.db.schema.Comentario;
import com.controlf.db.schema.Politico;
import com.controlf.db.schema.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@SpringBootTest
@Transactional
class AuthWorkflowTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

   private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PoliticoRepository politicoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

   @BeforeEach
void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
}

    @Test
    void shouldRegisterAndLoginUser() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nuevo@controlf.dev\",\"password\":\"Password123\",\"nombre\":\"Nuevo Usuario\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("nuevo@controlf.dev"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nuevo@controlf.dev\",\"password\":\"Password123\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", containsString("Bearer ")));
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"noexiste@controlf.dev\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowPublicGetWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/leyes/filtros"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyCitizenAccessToAdminEndpoints() throws Exception {
        Usuario ciudadano = new Usuario();
        ciudadano.setNombre("Ciudadano");
        ciudadano.setEmail("ciudadano@test.dev");
        ciudadano.setPasswordHash(passwordEncoder.encode("Password123"));
        ciudadano.setRol(Usuario.Rol.CIUDADANO);
        ciudadano.setActivo(true);
        usuarioRepository.save(ciudadano);

        String token = obtainToken("ciudadano@test.dev", "Password123");

        mockMvc.perform(get("/api/admin/panel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminAccessToAdminEndpoints() throws Exception {
        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setEmail("admin@test.dev");
        admin.setPasswordHash(passwordEncoder.encode("Password123"));
        admin.setRol(Usuario.Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);

        String token = obtainToken("admin@test.dev", "Password123");

        mockMvc.perform(get("/api/admin/panel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPreventCitizenFromEditingAnotherUsersComment() throws Exception {
        Usuario owner = new Usuario();
        owner.setNombre("Owner");
        owner.setEmail("owner@test.dev");
        owner.setPasswordHash(passwordEncoder.encode("Password123"));
        owner.setRol(Usuario.Rol.CIUDADANO);
        owner.setActivo(true);
        owner = usuarioRepository.save(owner);

        Usuario attacker = new Usuario();
        attacker.setNombre("Attacker");
        attacker.setEmail("attacker@test.dev");
        attacker.setPasswordHash(passwordEncoder.encode("Password123"));
        attacker.setRol(Usuario.Rol.CIUDADANO);
        attacker.setActivo(true);
        attacker = usuarioRepository.save(attacker);

        Comentario comentario = new Comentario();
        comentario.setTexto("Comentario del propietario");
        comentario.setUsuario(owner);
        comentario.setEsBasadoEnHechos(false);
        comentario.setFecha(java.time.LocalDateTime.now());
        comentarioRepository.save(comentario);

        String token = obtainToken("attacker@test.dev", "Password123");

        mockMvc.perform(patch("/api/politicos/comentarios/{comentarioId}", comentario.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"texto\":\"Intento de modificar\",\"usuarioId\":1}"))
                .andExpect(status().isForbidden());
    }

    private String obtainToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
package com.controlf.controller;

import com.controlf.db.repository.PoliticoRepository;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import com.controlf.db.repository.PromesaRepository;
import com.controlf.db.repository.UsuarioRepository;
import com.controlf.db.schema.Politico;
import com.controlf.db.schema.Promesa;
import com.controlf.db.schema.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class PoliticoPromesaControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PoliticoRepository politicoRepository;

    @Autowired
    private PromesaRepository promesaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

   @BeforeEach
void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
}

    @Test
    void adminCanCreatePromesa() throws Exception {
        Politico politico = savePolitico();
        String token = loginAndGetToken("admin-promesa@test.dev", "Password123", Usuario.Rol.ADMIN);

        mockMvc.perform(post("/api/politicos/{politicoId}/promesas", politico.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Promesa de prueba\",\"categoria\":\"Economía\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion").value("Promesa de prueba"))
                .andExpect(jsonPath("$.categoria").value("Economía"));
    }

    @Test
    void citizenCannotCreatePromesa() throws Exception {
        Politico politico = savePolitico();
        String token = loginAndGetToken("ciudadano-promesa@test.dev", "Password123", Usuario.Rol.CIUDADANO);

        mockMvc.perform(post("/api/politicos/{politicoId}/promesas", politico.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Promesa prohibida\",\"categoria\":\"Seguridad\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPromesaWithoutJwtReturnsUnauthorized() throws Exception {
        Politico politico = savePolitico();

        mockMvc.perform(post("/api/politicos/{politicoId}/promesas", politico.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Promesa sin token\",\"categoria\":\"Medio Ambiente\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPromesaForUnknownPoliticoReturnsNotFound() throws Exception {
        String token = loginAndGetToken("admin-promesa-404@test.dev", "Password123", Usuario.Rol.ADMIN);

        mockMvc.perform(post("/api/politicos/999999/promesas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Promesa inválida\",\"categoria\":\"Salud\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listPromesasWithoutJwtReturnsOk() throws Exception {
        Politico politico = savePolitico();
        Promesa promesa = new Promesa();
        promesa.setDescripcion("Promesa pública");
        promesa.setCategoria("Educación");
        promesa.setFechaCreacion(LocalDate.now());
        promesa.setPolitico(politico);
        promesaRepository.save(promesa);

        mockMvc.perform(get("/api/politicos/{politicoId}/promesas", politico.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcion").value("Promesa pública"));
    }

    @Test
    void citizenCanCreateComentario() throws Exception {
        Politico politico = savePolitico();
        String token = loginAndGetToken("ciudadano-comentario@test.dev", "Password123", Usuario.Rol.CIUDADANO);

        // El frontend envía únicamente { texto }; el usuario se toma del token JWT.
        mockMvc.perform(post("/api/politicos/{id}/comentarios", politico.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"texto\":\"Comentario ciudadano\"}"))
                .andExpect(status().isOk());
    }

    private Politico savePolitico() {
        Politico politico = new Politico();
        politico.setNombreCompleto("Político de prueba");
        politico.setPromesas(new ArrayList<>());
        politico.setComentarios(new ArrayList<>());
        politico.setCalificaciones(new ArrayList<>());
        return politicoRepository.save(politico);
    }

    private String loginAndGetToken(String email, String password, Usuario.Rol rol) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre(rol.name());
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("token")
                .asText();
    }
}

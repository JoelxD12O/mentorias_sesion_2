package com.example.sem3b_lab_1;

import com.example.sem3b_lab_1.dto.NewArtistaDto;
import com.example.sem3b_lab_1.artista.ArtistaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class ArtistaControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistaRepository artistaRepository;

    @Test
    void testCreateAndGetArtista() throws Exception {
        // Limpiar la base antes del test
        artistaRepository.deleteAll();

        // Crear artista
        NewArtistaDto dto = new NewArtistaDto();
        dto.setUsername("shakira");
        dto.setDescripcion("Artista colombiana");

        // POST /artista
        String response = mockMvc.perform(post("/artista")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("shakira"))
                .andReturn().getResponse().getContentAsString();

        // Obtener el id del artista creado
        Long id = artistaRepository.findByUsername("shakira").get().getId();

        // GET /artista/{id}
        mockMvc.perform(get("/artista/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("shakira"));
    }

    @Test
    void testGetArtista_NotFound() throws Exception {
        // GET /artista/99999 (no existe)
        mockMvc.perform(get("/artista/99999"))
                .andExpect(status().isNotFound());
    }
}


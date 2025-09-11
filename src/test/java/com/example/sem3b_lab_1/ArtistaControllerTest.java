package com.example.sem3b_lab_1;

import com.example.sem3b_lab_1.artista.Artista;
import com.example.sem3b_lab_1.artista.ArtistaController;
import com.example.sem3b_lab_1.artista.ArtistaRepository;
import com.example.sem3b_lab_1.dto.NewArtistaDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ArtistaControllerTest {

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

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ArtistaController artistaController;

    @BeforeEach
    void setup() {
        // Cargamos el controlador y el exception handler global
        mockMvc = MockMvcBuilders.standaloneSetup(artistaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ✅ Test crear artista (POST)
    @Test
    void testCreateArtista_Success() throws Exception {
        // DTO de entrada
        NewArtistaDto dto = new NewArtistaDto();
        dto.setUsername("shakira");

        // Entidad esperada
        Artista artista = new Artista();
        artista.setId(1L);
        artista.setUsername("shakira");

        // Simulamos que no existe previamente
        Mockito.when(artistaRepository.findByUsername("shakira"))
                .thenReturn(Optional.empty());

        // Simulamos el comportamiento de modelMapper.map(dto, artista)
        Mockito.doAnswer(invocation -> {
            NewArtistaDto source = invocation.getArgument(0);
            Artista target = invocation.getArgument(1);
            target.setUsername(source.getUsername());
            return null;
        }).when(modelMapper).map(Mockito.any(NewArtistaDto.class), Mockito.any(Artista.class));

        // Simulamos el guardado
        Mockito.when(artistaRepository.save(Mockito.any(Artista.class)))
                .thenReturn(artista);

        // Ejecutamos la petición y verificamos
        mockMvc.perform(post("/artista")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("shakira"));
    }

    // ✅ Test obtener artista inexistente (GET → 404)
    @Test
    void testGetArtista_NotFound() throws Exception {
        // Simulamos que no existe
        Mockito.when(artistaRepository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/artista/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("El artista 99 no fue encontrado"));
    }
}

package com.example.sem3b_lab_1.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// DTO para enviar información de un artista al cliente
@Data
public class ArtistaResponseDto {

    // Nombre de usuario del artista
    private String username;

    // Lista de IDs de canciones asociadas al artista
    private List<Long> cancionIdList = new ArrayList<>();
}

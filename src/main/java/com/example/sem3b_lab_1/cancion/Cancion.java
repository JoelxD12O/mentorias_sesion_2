package com.example.sem3b_lab_1.cancion;


import com.example.sem3b_lab_1.artista.Artista;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// Entidad Cancion que representa una canción en la base de datos
@Entity
@Data
public class Cancion {

    // Identificador único de la canción (clave primaria)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título de la canción
    private String titulo;

    // Duración de la canción en segundos
    private Integer duracion;

    // Relación muchos a muchos con la entidad Artista
    // Una canción puede tener varios artistas y un artista puede participar en varias canciones
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "cancion_artista",
            joinColumns = @JoinColumn(name = "cancion_id"),
            inverseJoinColumns = @JoinColumn(name = "artista_id")
    )
    private List<Artista> artistas = new ArrayList<>();


}

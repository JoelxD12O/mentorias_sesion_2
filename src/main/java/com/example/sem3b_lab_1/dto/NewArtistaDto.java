package com.example.sem3b_lab_1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewArtistaDto {

    @NotNull
    @Size(min = 1, max = 25)
    private String username;

    private String descripcion;

    @Email
    private String email;

}

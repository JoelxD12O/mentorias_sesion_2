package com.example.sem3b_lab_1;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Clase de configuración global para beans compartidos en la aplicación
@Configuration
public class GlobalConfig {

    // Bean para ModelMapper, útil para mapear objetos entre entidades y DTOs
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}

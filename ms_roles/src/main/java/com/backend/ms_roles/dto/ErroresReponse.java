package com.backend.ms_roles.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data 
@AllArgsConstructor 
public class ErroresReponse {
    private int status;
    private String mensaje;
    private LocalDateTime hora;
    private String ruta;
}

package com.backend.ms_roles.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long idUsuario;
    private String nombre;
    private String rol;
    private boolean debeCambiarClave;
}

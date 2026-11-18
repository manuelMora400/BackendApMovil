package com.backend.ms_roles.dto;

import com.backend.ms_roles.model.Rol;
import lombok.Data;

@Data
public class LoginResponse {
    private Long idUsuario;
    private Long nombre;
    private Rol rol;
    private boolean debeCambiarClave;
}

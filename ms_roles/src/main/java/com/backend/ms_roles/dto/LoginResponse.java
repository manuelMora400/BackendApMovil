package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Informacion devuelta después del inicio de sesion")
@Data
public class LoginResponse {

    @Schema(description = "Identificador del usuario", example = "1")
    private Long idUsuario;

    @Schema(description = "Nombre del usuario", example = "Manuel Mora")
    private String nombre;

    @Schema(description = "Rol asignado", example = "JEFE_TECNICO")
    private String rol;

    @Schema(description = "Indica si debe cambiar su contraseña", example = "true")
    private boolean debeCambiarClave;
}

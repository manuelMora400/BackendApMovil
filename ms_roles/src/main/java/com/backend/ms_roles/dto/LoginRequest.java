package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Credenciales para iniciar sesión")
public class LoginRequest {

    @Schema(description = "Correo electronico", example = "manuel@gmail.com")
    @NotBlank(message = "El correo es obligatorio")
    @Email
    @Size(max = 120)
    private String correo;

    @Schema(description = "Contraseña del usuario", example = "********")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 12)
    private String password;
}

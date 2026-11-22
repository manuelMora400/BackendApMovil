package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Solicitud para recuperar la contraseña")
@Data
public class RecuperarClaveRequest {

    @Schema(description = "RUT o correo electronico del usuario", example = "22256967-2")
    @NotBlank(message = "Debe ingresar su rut o correo")
    private String identificador;
}
package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos que el usuario puede modificar de su perfil")
public class ActualizarPerfilRequest {

    @Schema(description = "Nombre completo", example = "Manuel Mora Rojas")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no debe tener mas de 120 caracteres")
    private String nombre;

    @Schema(description = "Telefono de contacto", example = "+56931214567")
    @NotBlank(message = "El telefono es obligatorio")
    @Size(min = 9, max = 20, message = "El telefono debe tener entre 9 y 20 caracteres")
    private String telefono;

    @Schema(description = "Correo electronico (opcional, si no se envia no cambia)", example = "manuel@gmail.com")
    @Size(max = 120, message = "El correo no debe tener mas de 120 caracteres")
    private String correo;
}

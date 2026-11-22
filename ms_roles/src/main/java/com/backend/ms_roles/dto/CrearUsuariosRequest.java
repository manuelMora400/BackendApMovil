package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Datos necesarios para registrar un usuario")
@Data
public class CrearUsuariosRequest {

    @Schema(description = "RUT del usuario", example = "22256967-2")
    @Size(max = 12)
    @NotBlank(message = "El rut es obligatorio")
    private String rut;

    @Schema(description = "Nombre completo del usuario", example = "Manuel Mora")
    @Size(min = 10, max = 120)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Teléfono de contacto", example = "+56931214567")
    @Size(min = 10, max = 20)
    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @Schema(description = "Correo electrónico del usuario", example = "manuel@gmail.com")
    @Size(min = 10, max = 120)
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Schema(description = "Rol que tendra el usuario", example = "JEFE_TECNICO")
    private String rol;

    @Schema(description = "Giro de la empresa del cliente", example = "Empresa eléctrica")
    private String giro;

    @Schema(description = "Cargo del contacto", example = "Bodeguero")
    private String contactoCargo;
}
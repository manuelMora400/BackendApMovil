package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Información de respuesta del usuario")
@Data
public class UsuarioResponse {

    @Schema(description = "Identificador único", example = "1")
    private Long id;

    @Schema(description = "Nombre completo", example = "Manuel Mora")
    private String nombre;

    @Schema(description = "Correo electronico", example = "manuel@gmail.com")
    private String correo;

    @Schema(description = "RUT del usuario", example = "22256967-2")
    private String rut;

    @Schema(description = "Teléfono de contacto", example = "+56931214567")
    private String telefono;

    @Schema(description = "Rol asignado", example = "JEFE_TECNICO")
    private String rol;

    @Schema(description = "Estado de la cuenta", example = "true")
    private boolean activo;

    // nuevo: la pantalla Perfil de la app muestra el cargo (solo clientes; en los demas roles va null)
    @Schema(description = "Giro de la empresa (solo clientes)", example = "Agroindustria")
    private String giro;

    @Schema(description = "Cargo del contacto (solo clientes)", example = "Jefa de Mantenimiento")
    private String contactoCargo;

    @Schema(description = "Clave temporal generada para el usuario", example = "********")
    private String claveTemporal;
}
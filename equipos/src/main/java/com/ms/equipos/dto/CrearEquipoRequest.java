package com.ms.equipos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar un equipo de un cliente")
public class CrearEquipoRequest {

    @Schema(description = "TAG del equipo", example = "TR-03")
    @NotBlank(message = "El TAG es obligatorio")
    @Size(max = 20, message = "El TAG no puede superar 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "El TAG solo puede tener letras, numeros y guion (ej: TR-01)")
    private String tag;

    @Schema(description = "Tipo de equipo", example = "Transformador")
    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Size(min = 3, max = 40, message = "El tipo debe tener entre 3 a 40 caracteres")
    private String tipo;

    @Schema(description = "Instalacion donde esta el equipo", example = "Planta Lampa")
    @NotBlank(message = "La instalacion es obligatoria")
    @Size(min = 3, max = 80, message = "La instalacion debe tener entre 3 a 80 caracteres")
    private String instalacion;

    @Schema(description = "Id del usuario CLIENTE dueño del equipo (de ms_roles)", example = "4")
    @NotNull(message = "Seleccione el cliente dueño del equipo")
    @Positive(message = "Seleccione el cliente dueño del equipo")
    private Long idCliente;
}

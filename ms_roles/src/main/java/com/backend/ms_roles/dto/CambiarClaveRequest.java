package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Solicitud para cambiar la contraseña")
@Data
public class CambiarClaveRequest {

    @Schema(description = "Contraseña actual", example = "********")
    private String claveActual;

    @Schema(description = "Nueva contraseña", example = "********")
    @NotBlank(message = "La clave nueva es obligatoria")
    @Size(min = 8, max = 12)
    private String claveNueva;

    @Schema(description = "Confirmacion de la nueva contraseña", example = "********")
    @NotBlank(message = "La confirmacion de clave nueva es obligatoria")
    @Size(min = 8, max = 12)
    private String confirmarClave;
}
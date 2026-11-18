package com.backend.ms_roles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data 
public class CambiarClaveRequest {

    private String claveActual;

    @NotBlank(message = "La clave nueva es obligatoria")
    @Size(min = 8, max = 12)
    private String claveNueva;

    @NotBlank(message = "La confirmacion de clave nueva es obligatoria")
    @Size(min = 8, max = 12)
    private String confirmarClave;
}

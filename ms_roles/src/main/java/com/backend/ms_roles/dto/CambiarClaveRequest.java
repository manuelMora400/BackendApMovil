package com.backend.ms_roles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambiarClaveRequest {

    private String claveActual;

    @NotBlank(message = "La clave nueva es obligatoria")
    @Size(min = 8, max = 12)
    private String claveNueva;
}

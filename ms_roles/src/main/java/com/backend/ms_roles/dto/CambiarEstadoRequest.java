package com.backend.ms_roles.dto;

import jakarta.validation.constraints.NotNull;

public class CambiarEstadoRequest {

    @NotNull(message = "No puede ser nulo")
    private boolean activo;
}

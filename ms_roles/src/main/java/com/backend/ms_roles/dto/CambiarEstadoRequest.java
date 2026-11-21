package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para modificar el estado de una cuenta")
public class CambiarEstadoRequest {

    @Schema(description = "Estado que tendra la cuenta", example = "true")
    @NotNull(message = "No puede ser nulo")
    private boolean activo;
}
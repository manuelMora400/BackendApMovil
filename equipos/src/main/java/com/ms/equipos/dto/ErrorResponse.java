package com.ms.equipos.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Informacion del error producido por la API")
public class ErrorResponse {
    @Schema(description = "Codigo HTTP del error", example = "409")
    private int status;

    @Schema(description = "Descripcion del error", example = "Ya existe el TAG TR-01 en Planta Lampa")
    private String mensaje;

    @Schema(description = "Fecha y hora del error", example = "2026-11-23T19:30:00")
    private LocalDateTime hora;

    @Schema(description = "Ruta donde ocurrio el error", example = "/api/equipos")
    private String ruta;

    @JsonProperty("message")
    @Schema(hidden = true)
    public String getMessage() {
        return mensaje;
    }
}

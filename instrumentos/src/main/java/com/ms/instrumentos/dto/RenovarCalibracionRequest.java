package com.ms.instrumentos.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "Fechas de la nueva calibracion del instrumento")
public class RenovarCalibracionRequest {

    @Schema(description = "Fecha de la nueva calibracion", example = "2026-11-23")
    @NotNull(message = "La fecha de calibracion del instrumento es obligatoria")
    private LocalDate fechaCalibracion;

    @Schema(description = "Fecha de vencimiento de la nueva calibracion", example = "2027-11-23")
    @NotNull(message = "La fecha de vencimiento del instrumento es obligatoria")
    private LocalDate fechaVencimiento;
}

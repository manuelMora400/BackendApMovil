package com.ms.instrumentos.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearInstrumentoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La marca es obligatorio")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotBlank(message = "El Numero de serie es obligatorio")
    private String numeroSerie;

    @NotNull(message = "La fecha de calibracion del instrumento es obligatoria")
    private LocalDate fechaCalibracion;

    @NotNull(message = "La fecha de vencimiento del instrumento es obligatoria")
    private LocalDate fechaVencimiento;
}

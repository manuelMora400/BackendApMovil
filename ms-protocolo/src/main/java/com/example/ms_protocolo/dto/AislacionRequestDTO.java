package com.example.ms_protocolo.dto;

import com.example.ms_protocolo.model.EnumDesvanado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos de una prueba de aislacion de un protocolo")
public class AislacionRequestDTO {

    @Schema(description = "TAG del equipo", example = "TR-01")
    @NotBlank(message = "El TAG es obligatorio")
    private String tag;

    @Schema(description = "Instalacion del equipo", example = "Planta Lampa")
    @NotBlank(message = "La instalacion es obligatoria")
    private String instalacion;

    @Schema(description = "Devanado medido", example = "AT_BT")
    @NotNull(message = "El devanado es obligatorio")
    private EnumDesvanado enumDevanado;

    @Schema(description = "Tension de prueba (kV)", example = "5.0")
    private Double tension;

    @Schema(description = "Lectura al minuto 1", example = "1200.0")
    private Double lectura1Min;

    @Schema(description = "Lectura al minuto 10", example = "2400.0")
    private Double lectura10Min;

    @Schema(description = "Id del protocolo al que pertenece", example = "1")
    @NotNull(message = "El protocolo es obligatorio")
    private Long idProtocolo;
}

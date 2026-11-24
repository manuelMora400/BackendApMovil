package com.example.ms_protocolo.dto;

import java.time.LocalDateTime;

import com.example.ms_protocolo.model.EnumResultado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para registrar un protocolo en terreno (queda PENDIENTE hasta que el ingeniero lo formalice)")
public class ProtocoloRequestDTO {

    @Schema(description = "Fecha y hora de la prueba (si no se envia, se usa la actual)", example = "2026-11-24T10:30:00")
    private LocalDateTime fechaPrueba;

    @Schema(description = "Temperatura ambiente", example = "21.5")
    private Double temperatura;

    @Schema(description = "Humedad relativa", example = "50.0")
    private Double humedad;

    @Schema(description = "Latitud de la prueba", example = "-33.4489")
    private Double latitud;

    @Schema(description = "Longitud de la prueba", example = "-70.6693")
    private Double longitud;

    @Schema(description = "Resultado de la prueba", example = "APROBADO")
    @NotNull(message = "El resultado de la prueba es obligatorio")
    private EnumResultado resultadoPrueba;

    @Schema(description = "Id del tecnico que hizo la prueba (ms-usuarios)", example = "3")
    private Long idTecnico;

    @Schema(description = "Id del instrumento usado (ms-instrumentos); debe estar vigente", example = "1")
    private Long idInstrumento;
}

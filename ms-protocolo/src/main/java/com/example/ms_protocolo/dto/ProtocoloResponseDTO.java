package com.example.ms_protocolo.dto;

import java.time.LocalDateTime;

import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.model.EnumResultado;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Schema(description = "Modelo de respuesta con la informacion de un protocolo")
public class ProtocoloResponseDTO {

    @Schema(description = "Identificador unico del protocolo", example = "1")
    private Long idProtocolo;

    @Schema(description = "Fecha y hora en que se realizo la prueba", example = "2026-11-20 10:30")
    private LocalDateTime fechaPrueba;

    @Schema(description = "Temperatura ambiente registrada durante la prueba", example = "22.5")
    private Double temperatura;

    @Schema(description = "Humedad relativa registrada durante la prueba", example = "55.0")
    private Double humedad;

    @Schema(description = "Latitud geografica de la ubicacion de la prueba", example = "-33.4489")
    private Double latitud;

    @Schema(description = "Longitud geografica de la ubicacion de la prueba", example = "-70.6693")
    private Double longitud;

    @Schema(description = "Estado del protocolo: PENDIENTE o FORMALIZADO", example = "PENDIENTE")
    private EnumEstado estadoProtocolo;

    @Schema(description = "Resultado de la prueba", example = "APROBADO")
    private EnumResultado resultadoPrueba;

    @Schema(description = "Id del tecnico que hizo la prueba", example = "3")
    private Long idTecnico;

    @Schema(description = "Id del instrumento usado", example = "1")
    private Long idInstrumento;
}
package com.example.ms_protocolo.dto;

import com.example.ms_protocolo.model.EnumDesvanado;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Schema (description = "Modelo de respuesta con la informacion de una prueba de aislacion e instalacion")
public class AislacionResponseDTO {

    @Schema(description = "Identificador unico de la prueba de aislacion", example = "1")
    private Long id;

    @Schema(description = "Codigo o tag del equipo o prueba", example = "TAG-TR-01")
    private String tag;

    @Schema(description = "Nombre o identificacion de la instalacion", example = "Subestacion Norte")
    private String instalacion;

    @Schema(description = "Tipo de devanado evaluado en la prueba", example = "ALTA_TENSION")
    private EnumDesvanado enumDevanado;

    @Schema(description = "Tension aplicada durante la medicion", example = "5.0")
    private Double tension;

    @Schema(description = "Lectura de resistencia de aislacion al minuto 1", example = "1200.0")
    private Double lectura1Min;

    @Schema(description = "Lectura de resistencia de aislacion al minuto 10", example = "2400.0")
    private Double lectura10Min;

    @Schema(description = "Identificador del protocolo asociado", example = "1")
    private Long idProtocolo;
}
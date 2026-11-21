package com.example.ms_protocolo.dto;

import com.example.ms_protocolo.model.EnumDesvanado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class AislacionRequestDTO {

    private String tag;
    private String instalacion;
    private EnumDesvanado enumDevanado;
    private Double tension;
    private Double lectura1Min;
    private Double lectura10Min;
    private Long idProtocolo;
}
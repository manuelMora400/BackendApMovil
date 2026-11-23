package com.example.ms_protocolo.dto;

import java.time.LocalDateTime;

import com.example.ms_protocolo.model.EnumEstado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class ProtocoloRequestDTO {

    private LocalDateTime fechaPrueba;
    private Double temperatura;
    private Double humedad;
    private Double latitud;
    private Double longitud;
    private EnumEstado estadoProtocolo;
}
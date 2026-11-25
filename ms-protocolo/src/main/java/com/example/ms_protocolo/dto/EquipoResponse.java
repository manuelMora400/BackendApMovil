package com.example.ms_protocolo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipoResponse {

    private Long idEquipo;
    private String tag;
    private String instalacion;
}
package com.example.ms_protocolo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VigenciaResponse {

    private Long idInstrumento;
    private boolean vigente;
}
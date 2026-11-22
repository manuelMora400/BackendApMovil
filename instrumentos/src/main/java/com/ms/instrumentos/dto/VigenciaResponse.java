package com.ms.instrumentos.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VigenciaResponse {

    private Long idInstrumento;
    private Boolean vigente;
    private LocalDate fechaVencimiento;
}

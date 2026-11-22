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
public class InstrumentoResponse {
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private LocalDate fechaCalibracion;
    private LocalDate fechaVencimiento;
    private boolean vigente;
}

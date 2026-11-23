package com.ms.equipos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipoResponse {
    private Long id;
    private String tag;
    private String tipo;
    private String instalacion;
    private Long idCliente;
}

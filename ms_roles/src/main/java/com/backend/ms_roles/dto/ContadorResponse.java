package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Cantidad de registros")
public class ContadorResponse {

    @Schema(description = "Cantidad", example = "6")
    private Long contador;
}

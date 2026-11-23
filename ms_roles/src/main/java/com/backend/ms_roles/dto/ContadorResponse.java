package com.backend.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Igual que ContadorResponse de ms_instrumentos: un solo campo "contador"
// asi la app usa la misma data class para los 2 contadores del panel Jefe
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Cantidad de registros")
public class ContadorResponse {

    @Schema(description = "Cantidad", example = "6")
    private Long contador;
}

package com.example.ms_protocolo.dto;

import com.example.ms_protocolo.model.EnumEstado;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CambiarEstadoRequestDTO {

    @NotNull(message = "El estado es obligatorio")
    private EnumEstado estadoProtocolo;
}

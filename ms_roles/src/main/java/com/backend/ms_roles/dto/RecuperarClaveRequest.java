package com.backend.ms_roles.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class RecuperarClaveRequest {

    @NotBlank(message = "Debe ingresar su rut o correo")
    private String identificador;
}

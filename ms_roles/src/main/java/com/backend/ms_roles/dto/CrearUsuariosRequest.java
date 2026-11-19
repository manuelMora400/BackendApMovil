package com.backend.ms_roles.dto;

import com.backend.ms_roles.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearUsuariosRequest {

    @Size(max = 12)
    @NotBlank(message = "El rut es obligatorio")
    private String rut;

    @Size(min = 10, max = 120)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Size(min = 10, max = 20)
    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @Size(min = 10, max = 120)
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    private String rol;
    private String giro;
    private String contactoCargo;

}
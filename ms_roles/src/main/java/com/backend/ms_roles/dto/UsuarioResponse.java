package com.backend.ms_roles.dto;

import lombok.Data;

@Data 
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String rut;
    private String telefono;
    private String rol;
    private boolean activo;
    private String claveTemporal;
}

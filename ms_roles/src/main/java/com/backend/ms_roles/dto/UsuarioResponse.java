package com.backend.ms_roles.dto;

import com.backend.ms_roles.model.Rol;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String rut;
    private String telefono;
    private Rol rol;
    private boolean activo;
}

package com.backend.ms_roles.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.ms_roles.dto.CambiarClaveRequest;
import com.backend.ms_roles.dto.CrearUsuariosRequest;
import com.backend.ms_roles.dto.LoginRequest;
import com.backend.ms_roles.dto.LoginResponse;
import com.backend.ms_roles.dto.RecuperarClaveRequest;
import com.backend.ms_roles.dto.UsuarioResponse;
import com.backend.ms_roles.service.UsuarioServices;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioServices services;

    public UsuarioController(UsuarioServices services) {
        this.services = services;
    }

    @PostMapping()
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody CrearUsuariosRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(services.crearUsuario(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(services.login(request));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(services.listarUsuarios());
    }

    @GetMapping("/obtenerPerfil/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(services.obtenerPerfil(id));
    }

    @PostMapping("/cambiarEstado/{id}")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(services.cambiarEstado(id));
    }

    @PutMapping("/cambiarClave/{id}")
    public ResponseEntity<UsuarioResponse> cambiarClave(@Valid @PathVariable Long id,@RequestBody CambiarClaveRequest request) {
        services.cambiarClave(id, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/recuperar-clave")
    public  ResponseEntity<UsuarioResponse> recuperarClave (@Valid @RequestBody RecuperarClaveRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(services.recuperarClave(request));
    }
    
}

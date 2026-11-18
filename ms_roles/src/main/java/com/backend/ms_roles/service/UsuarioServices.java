package com.backend.ms_roles.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.ms_roles.dto.CrearUsuariosRequest;
import com.backend.ms_roles.dto.LoginRequest;
import com.backend.ms_roles.dto.LoginResponse;
import com.backend.ms_roles.dto.UsuarioResponse;
import com.backend.ms_roles.model.Rol;
import com.backend.ms_roles.model.Usuario;
import com.backend.ms_roles.repository.RolRepository;
import com.backend.ms_roles.repository.UsuarioRepository;
import com.backend.ms_roles.security.GeneradorClaveTemporal;

@Service
public class UsuarioServices {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServices(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private LoginResponse mapToDTO(Usuario usuario) {
        LoginResponse dto = new LoginResponse();
        dto.setIdUsuario(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setRol(usuario.getRol().getNombre());
        dto.setDebeCambiarClave(usuario.getCambiarClave());
        return dto;
    }

    private UsuarioResponse mapToDTOcreaResponse(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setRut(usuario.getRut());
        dto.setTelefono(usuario.getTelefono());
        dto.setRol(usuario.getRol().getNombre());
        dto.setActivo(usuario.getActivo());
        return dto;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario usuarioEcontrado = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciaes Invalidas"));
        if (!usuarioEcontrado.getActivo()) {
            throw new RuntimeException("Credenciales invalidas");
        }

        // verificar si la clave script vs la escrita coincidan
        if (!passwordEncoder.matches(request.getPassword(), usuarioEcontrado.getPassword())) {
            throw new RuntimeException("Credenciales Invalidas");
        }
        return mapToDTO(usuarioEcontrado);
    }

    public UsuarioResponse crearUsuario(CrearUsuariosRequest request) {

        // Si rol -> cliente (contenga giro / contacto cargo)
        if ("CLIENTE".equalsIgnoreCase(request.getRol())) {

            // validacion de giro (Solo clientes)
            if (request.getGiro() == null || request.getGiro().isBlank()) {
                throw new RuntimeException("El giro para Cliente es obligatorio");
            } else if (request.getGiro().length() < 10 || request.getGiro().length() > 80) {
                throw new RuntimeException("El giro tiene que estar entre 10 a 80 caracteres");
            }

            // validacion contacto cargo solo cliente
            if (request.getContactoCargo() == null || request.getContactoCargo().isBlank()) {
                throw new RuntimeException("El catacto cargo para Cliente es obligatorio");
            }
        }
        // salimos de la condicion cliente

        // rut o correo si existe
        if (usuarioRepository.existsByCorreo(request.getCorreo().trim())) {
            throw new RuntimeException("El correo ya existe en el sistema");
        } else if (usuarioRepository.existsByRut(request.getRut().trim())) {
            throw new RuntimeException("El rut ya existe en el sistema");
        }

        // validamos rol
        Rol rolEcontrado = rolRepository.findByNombre(request.getRol().trim().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Rol no existente"));

        // generamos clave
        String claveTemporal = GeneradorClaveTemporal.generarClave();
        String claveScriptadaTemporal = passwordEncoder.encode(claveTemporal); // La encriptamos

        Usuario nuevoUsuario = new Usuario();

        nuevoUsuario.setRol(rolEcontrado);
        nuevoUsuario.setPassword(claveScriptadaTemporal);
        nuevoUsuario.setNombre(request.getNombre().trim());
        nuevoUsuario.setRut(request.getRut().trim());
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setCambiarClave(true);
        nuevoUsuario.setCorreo(request.getCorreo().trim());
        nuevoUsuario.setTelefono(request.getTelefono().trim());
        if ("CLIENTE".equalsIgnoreCase(request.getRol())) {
            nuevoUsuario.setGiro(request.getGiro().trim());
            nuevoUsuario.setContactoCargo(request.getContactoCargo().trim());
        } else {
            nuevoUsuario.setGiro(null);
            nuevoUsuario.setContactoCargo(null);
        }

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        UsuarioResponse respuestaClave = mapToDTOcreaResponse(usuarioGuardado);
        respuestaClave.setClaveTemporal(claveTemporal);

        return respuestaClave;
    }

    public List<UsuarioResponse> listarUsuarios() {
        List<UsuarioResponse> usuario = usuarioRepository.findAll().stream().map(this::mapToDTOcreaResponse).toList();
        return usuario;
    }
}

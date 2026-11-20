package com.backend.ms_roles.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.backend.ms_roles.dto.*;
import com.backend.ms_roles.exception.*;
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
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciaes Invalidas"));
        if (!usuarioEcontrado.getActivo()) {
            throw new CredencialesInvalidasException("Credenciales invalidas");
        }

        // verificar si la clave script vs la escrita coincidan
        if (!passwordEncoder.matches(request.getPassword(), usuarioEcontrado.getPassword())) {
            throw new CredencialesInvalidasException("Credenciales Invalidas");
        }
        return mapToDTO(usuarioEcontrado);
    }

    public UsuarioResponse crearUsuario(CrearUsuariosRequest request) {

        // Si rol -> cliente (contenga giro / contacto cargo)
        if ("CLIENTE".equalsIgnoreCase(request.getRol())) {

            // validacion de giro (Solo clientes)
            if (request.getGiro() == null || request.getGiro().isBlank()) {
                throw new ReglasNegocioException("El giro para Cliente es obligatorio");
            } else if (request.getGiro().length() < 10 || request.getGiro().length() > 80) {
                throw new ReglasNegocioException("El giro tiene que estar entre 10 a 80 caracteres");
            }

            // validacion contacto cargo solo cliente
            if (request.getContactoCargo() == null || request.getContactoCargo().isBlank()) {
                throw new ReglasNegocioException("El catacto cargo para Cliente es obligatorio");
            }
        }
        // salimos de la condicion cliente

        // rut o correo si existe
        if (usuarioRepository.existsByCorreo(request.getCorreo().trim())) {
            throw new DuplicadosException("El correo ya existe en el sistema");
        } else if (usuarioRepository.existsByRut(request.getRut().trim())) {
            throw new DuplicadosException("El rut ya existe en el sistema");
        }

        // validamos rol
        Rol rolEcontrado = rolRepository.findByNombre(request.getRol().trim().toUpperCase())
                .orElseThrow(() -> new NoEncontradoException("Rol no existente"));

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

    public UsuarioResponse obtenerPerfil(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario == null || usuario.isEmpty()) {
            throw new NoEncontradoException("El usuario no existe");
        }
        Usuario usuarioEncontrado = usuario.get();
        return mapToDTOcreaResponse(usuarioEncontrado);
    }

    public UsuarioResponse cambiarEstado(Long id) {

        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario == null || usuario.isEmpty()) {
            throw new NoEncontradoException("El usuario a desactivar no existe");
        }

        Usuario usuarioCambia = usuario.get();
        usuarioCambia.setActivo(!usuarioCambia.getActivo());

        Usuario usuarioCambiado = usuarioRepository.save(usuarioCambia);
        return mapToDTOcreaResponse(usuarioCambiado);
    }

    public void cambiarClave(Long id, CambiarClaveRequest request) {

        Optional<Usuario> buscarUsuario = usuarioRepository.findById(id);

        if (buscarUsuario == null || buscarUsuario.isEmpty()) {
            throw new NoEncontradoException("El Usuario no exite");
        }

        Usuario usuarioEcontrado = buscarUsuario.get();

        // verificamos que la contraseña sea no igual
        if (passwordEncoder.matches(request.getClaveNueva(), usuarioEcontrado.getPassword())) {
            throw new ReglasNegocioException("La contraseña no debe ser igual al anterior");
        }
        if (request.getClaveNueva().length() < 8 || request.getClaveNueva().length() > 12) {
            throw new ReglasNegocioException("La contraseña debe tener un largo de 8 a 12 caracteres");
        }
        if (request.getClaveNueva() == null || request.getClaveNueva().isBlank()) {
            throw new ReglasNegocioException("La contraseña es obligatoria");
        }
        if (!request.getClaveNueva().matches(".*[A-Z].*")) {
            throw new ReglasNegocioException("La contraseña debe contener al menos una letra en mayúscula");
        }
        if (!request.getClaveNueva().matches(".*[.-].*")) {
            throw new ReglasNegocioException("La contraseña debe contener al menos un punto ( . ) o guion ( - )");
        }

        usuarioEcontrado.setPassword(passwordEncoder.encode(request.getClaveNueva()));
        usuarioEcontrado.setCambiarClave(false);

        usuarioRepository.save(usuarioEcontrado);
    }

    public UsuarioResponse recuperarClave(RecuperarClaveRequest recuperarClaveRequest) {

        String identificador = recuperarClaveRequest.getIdentificador();
        Optional<Usuario> buscar = usuarioRepository.findByRutOrCorreo(identificador, identificador);

        if (buscar == null || buscar.isEmpty()) {
            throw new NoEncontradoException("No existe el usuario con ese RUT y correo");
        }

        Usuario usuario = buscar.get();

        if (!usuario.getActivo()) {
            throw new NoEncontradoException("El usuario esta inactivo");
        }

        // generamos clave
        String claveTemporal = GeneradorClaveTemporal.generarClave();
        String claveScriptadaTemporal = passwordEncoder.encode(claveTemporal); // La encriptamos

        usuario.setPassword(claveScriptadaTemporal);
        usuario.setCambiarClave(true); // se habilita que pueda cambiar contraseña
        UsuarioResponse response = mapToDTOcreaResponse(usuario);
        response.setClaveTemporal(claveTemporal);

        usuarioRepository.save(usuario);

        return response;
    }

}

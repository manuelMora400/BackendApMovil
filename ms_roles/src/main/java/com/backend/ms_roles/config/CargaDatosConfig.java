package com.backend.ms_roles.config;

import com.backend.ms_roles.model.Rol;
import com.backend.ms_roles.model.Usuario;
import com.backend.ms_roles.repository.RolRepository;
import com.backend.ms_roles.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CargaDatosConfig {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Rol jefe = obtenerOCrearRol(rolRepository, "JEFE_TECNICO");
            Rol ingeniero = obtenerOCrearRol(rolRepository, "INGENIERO");
            Rol tecnico = obtenerOCrearRol(rolRepository, "TECNICO");
            Rol cliente = obtenerOCrearRol(rolRepository, "CLIENTE");

            crearSiNoExiste(usuarioRepository, passwordEncoder, jefe,
                    "Ricardo Espinoza Lagos", "respinoza@induztek.cl", "13.871.905-7",
                    "987541666", "Lolsitox-.", "Giro", "Cargo");
            crearSiNoExiste(usuarioRepository, passwordEncoder, ingeniero,
                    "Andrea Contreras Silva", "acontreras@induztek.cl", "15.906.334-8",
                    "987541666", "Cololo.-892", "Giro", "Cargo");
            crearSiNoExiste(usuarioRepository, passwordEncoder, tecnico,
                    "Valentina Soto", "vsoto@induztek.cl", "18.871.905-7",
                    "987541666", "Ude.-OloDE", null, null);
            crearSiNoExiste(usuarioRepository, passwordEncoder, cliente,
                    "Marcela Fuentes Rojas", "mfuentes@envpacifico.ejemplo", "14.226.791-8",
                    "+56900000101", "Cliente.-01", "Agroindustria", "Jefa de Mantenimiento");
            crearSiNoExiste(usuarioRepository, passwordEncoder, cliente,
                    "Tomas Leiva Araya", "tleiva@losaromos.ejemplo", "18.390.452-3",
                    "+56900000104", "Cliente.-02", "Salud", "Supervisor Electrico");
            crearSiNoExiste(usuarioRepository, passwordEncoder, cliente,
                    "Patricio Navarro Soto", "pnavarro@cerroalto.ejemplo", "12.645.308-6",
                    "+56900000103", "Cliente.-03", "Mineria", "Jefe de Mantenimiento Electrico");
        };
    }

    private Rol obtenerOCrearRol(RolRepository rolRepository, String nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            long siguienteId = rolRepository.findAll().stream()
                    .mapToLong(Rol::getId_rol)
                    .max()
                    .orElse(0L) + 1;
            return rolRepository.save(new Rol(siguienteId, nombre));
        });
    }

    private void crearSiNoExiste(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            Rol rol,
            String nombre,
            String correo,
            String rut,
            String telefono,
            String clave,
            String giro,
            String contactoCargo) {

        if (usuarioRepository.existsByCorreo(correo) || usuarioRepository.existsByRut(rut)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setRut(rut);
        usuario.setTelefono(telefono);
        usuario.setPassword(passwordEncoder.encode(clave));
        usuario.setGiro(giro);
        usuario.setContactoCargo(contactoCargo);
        usuario.setActivo(true);
        usuario.setCambiarClave(false);
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}

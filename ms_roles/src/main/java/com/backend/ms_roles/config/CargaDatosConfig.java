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
            Rol rolAdmin = rolRepository.findById(1L).orElseGet(() -> {
                Rol r = new Rol(1L, "JEFE_TECNICO");
                return rolRepository.save(r);
            });

            Rol rolCliente = rolRepository.findById(2L).orElseGet(() -> {
                Rol r = new Rol(2L, "CLIENTE");
                return rolRepository.save(r);
            });

            if (usuarioRepository.count() == 0) {

                Usuario admin = new Usuario();
                admin.setNombre("Admin Prueba");
                admin.setRut("11.111.111-1");
                admin.setCorreo("jefe@electric.cl");
                admin.setPassword(passwordEncoder.encode("Jefe.123"));
                admin.setRol(rolAdmin);
                admin.setTelefono("912345678");
                admin.setActivo(true);
                usuarioRepository.save(admin);

                Usuario cliente = new Usuario();
                cliente.setNombre("Cliente Prueba");
                cliente.setRut("22.222.222-2");
                cliente.setCorreo("cliente@electric.cl");
                cliente.setPassword(passwordEncoder.encode("Cliente.123"));
                cliente.setRol(rolCliente);
                cliente.setTelefono("987654321");
                cliente.setActivo(true);
                usuarioRepository.save(cliente);
            }
        };
    }
}
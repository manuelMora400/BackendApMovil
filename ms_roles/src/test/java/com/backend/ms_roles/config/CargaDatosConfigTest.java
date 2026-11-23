package com.backend.ms_roles.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.backend.ms_roles.model.Rol;
import com.backend.ms_roles.model.Usuario;
import com.backend.ms_roles.repository.RolRepository;
import com.backend.ms_roles.repository.UsuarioRepository;

// Prueba las semillas sin base de datos: repositories y encoder son mocks
class CargaDatosConfigTest {

    @Test
    @DisplayName("con la base vacia crea los 4 roles y los 6 usuarios")
    void cargaRolesYUsuarios() throws Exception {
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        RolRepository roles = mock(RolRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(roles.findByNombre(anyString())).thenReturn(Optional.empty());
        when(roles.findAll()).thenReturn(List.of());
        when(roles.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarios.existsByCorreo(anyString())).thenReturn(false);
        when(usuarios.existsByRut(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("hash");

        new CargaDatosConfig().initDatabase(usuarios, roles, encoder).run();

        verify(roles, times(4)).save(any(Rol.class));
        verify(usuarios, times(6)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("si roles y usuarios ya existen no se duplican al reiniciar")
    void noDuplica() throws Exception {
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        RolRepository roles = mock(RolRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(roles.findByNombre(anyString())).thenReturn(Optional.of(new Rol(1L, "JEFE_TECNICO")));
        when(usuarios.existsByCorreo(anyString())).thenReturn(true);

        new CargaDatosConfig().initDatabase(usuarios, roles, encoder).run();

        verify(roles, never()).save(any(Rol.class));
        verify(usuarios, never()).save(any(Usuario.class));
    }
}

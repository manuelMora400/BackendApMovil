package com.backend.ms_roles.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.ms_roles.dto.CambiarClaveRequest;
import com.backend.ms_roles.dto.ContadorResponse;
import com.backend.ms_roles.dto.LoginResponse;
import com.backend.ms_roles.dto.UsuarioResponse;
import com.backend.ms_roles.exception.CredencialesInvalidasException;
import com.backend.ms_roles.exception.DuplicadosException;
import com.backend.ms_roles.exception.NoEncontradoException;
import com.backend.ms_roles.exception.ReglasNegocioException;
import com.backend.ms_roles.repository.RolRepository;
import com.backend.ms_roles.repository.UsuarioRepository;
import com.backend.ms_roles.service.UsuarioServices;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioServices services;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private RolRepository rolRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private static final String JSON_CREAR =
            "{\"rut\":\"12345678-9\",\"nombre\":\"Valentina Soto Perez\",\"telefono\":\"+56912345678\","
            + "\"correo\":\"vsoto@induztek.cl\",\"rol\":\"TECNICO\"}";

    private static final String JSON_LOGIN = "{\"correo\":\"vsoto@induztek.cl\",\"password\":\"Ude.-OloDE\"}";

    private static final String JSON_CLAVE =
            "{\"claveActual\":\"Temp.12345\",\"claveNueva\":\"Nueva.123\",\"confirmarClave\":\"Nueva.123\"}";

    private UsuarioResponse usuario() {
        UsuarioResponse u = new UsuarioResponse();
        u.setId(3L);
        u.setNombre("Valentina Soto");
        u.setCorreo("vsoto@induztek.cl");
        u.setRut("18.871.905-7");
        u.setTelefono("987541666");
        u.setRol("TECNICO");
        u.setActivo(true);
        return u;
    }


    @Test
    @DisplayName("POST /api/usuarios valido -> 201")
    void crear_ok() throws Exception {
        UsuarioResponse creado = usuario();
        creado.setClaveTemporal("Kx7aP2m.48");
        when(services.crearUsuario(any())).thenReturn(creado);

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claveTemporal").value("Kx7aP2m.48"));
    }

    @Test
    @DisplayName("POST /api/usuarios con RUT mal escrito -> 400 con el mensaje del formato")
    void crear_rutInvalido() throws Exception {
        String json = JSON_CREAR.replace("12345678-9", "12345678K");

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Formato de RUT invalido (ej: 12345678-K o 12.345.678-K)"));
        verify(services, never()).crearUsuario(any());
    }

    @Test
    @DisplayName("POST /api/usuarios con nombre con numeros -> 400")
    void crear_nombreConNumeros() throws Exception {
        String json = JSON_CREAR.replace("Valentina Soto Perez", "Valentina 2 Soto P");

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El nombre no debe contener numeros"));
    }

    @Test
    @DisplayName("POST /api/usuarios con correo repetido -> 409")
    void crear_duplicado() throws Exception {
        when(services.crearUsuario(any())).thenThrow(new DuplicadosException("El correo ya existe en el sistema"));

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El correo ya existe en el sistema"));
    }

    @Test
    @DisplayName("POST /api/usuarios con rol inexistente -> 404")
    void crear_rolNoExiste() throws Exception {
        when(services.crearUsuario(any())).thenThrow(new NoEncontradoException("Rol no existente"));

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("POST /api/usuarios/login correcto -> 200")
    void login_ok() throws Exception {
        LoginResponse r = new LoginResponse();
        r.setIdUsuario(3L);
        r.setNombre("Valentina Soto");
        r.setRol("TECNICO");
        r.setDebeCambiarClave(false);
        when(services.login(any())).thenReturn(r);

        mockMvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON).content(JSON_LOGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("TECNICO"))
                .andExpect(jsonPath("$.debeCambiarClave").value(false));
    }

    @Test
    @DisplayName("POST /api/usuarios/login incorrecto -> 401")
    void login_incorrecto() throws Exception {
        when(services.login(any())).thenThrow(new CredencialesInvalidasException("Credenciales invalidas"));

        mockMvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON).content(JSON_LOGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales invalidas"));
    }


    @Test
    @DisplayName("GET /api/usuarios/listar -> 200")
    void listar() throws Exception {
        when(services.listarUsuarios()).thenReturn(List.of(usuario()));

        mockMvc.perform(get("/api/usuarios/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("vsoto@induztek.cl"));
    }

    @Test
    @DisplayName("GET /api/usuarios/activos/cantidad -> 200 con el campo contador")
    void cantidadActivas() throws Exception {
        when(services.contarActivas()).thenReturn(new ContadorResponse(6L));

        mockMvc.perform(get("/api/usuarios/activos/cantidad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contador").value(6));
    }

    @Test
    @DisplayName("GET /api/usuarios/obtenerPerfil/{id} -> 200")
    void perfil_ok() throws Exception {
        when(services.obtenerPerfil(3L)).thenReturn(usuario());

        mockMvc.perform(get("/api/usuarios/obtenerPerfil/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Valentina Soto"));
    }

    @Test
    @DisplayName("GET /api/usuarios/obtenerPerfil/{id} inexistente -> 404")
    void perfil_noExiste() throws Exception {
        when(services.obtenerPerfil(99L)).thenThrow(new NoEncontradoException("El usuario no existe"));

        mockMvc.perform(get("/api/usuarios/obtenerPerfil/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }

    @Test
    @DisplayName("POST /api/usuarios/cambiarEstado/{id} -> 200")
    void cambiarEstado() throws Exception {
        UsuarioResponse desactivado = usuario();
        desactivado.setActivo(false);
        when(services.cambiarEstado(3L)).thenReturn(desactivado);

        mockMvc.perform(post("/api/usuarios/cambiarEstado/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }


    @Test
    @DisplayName("PUT /api/usuarios/cambiarClave/{id} correcto -> 200")
    void cambiarClave_ok() throws Exception {
        mockMvc.perform(put("/api/usuarios/cambiarClave/3").contentType(MediaType.APPLICATION_JSON).content(JSON_CLAVE))
                .andExpect(status().isOk());
        verify(services).cambiarClave(eq(3L), any(CambiarClaveRequest.class));
    }

    @Test
    @DisplayName("PUT /api/usuarios/cambiarClave/{id} con clave actual incorrecta -> 400")
    void cambiarClave_reglaNegocio() throws Exception {
        doThrow(new ReglasNegocioException("La contraseña actual es incorrecta"))
                .when(services).cambiarClave(eq(3L), any(CambiarClaveRequest.class));

        mockMvc.perform(put("/api/usuarios/cambiarClave/3").contentType(MediaType.APPLICATION_JSON).content(JSON_CLAVE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La contraseña actual es incorrecta"));
    }

    @Test
    @DisplayName("PUT /api/usuarios/actualizar/{id} -> 200")
    void actualizarPerfil() throws Exception {
        when(services.actualizarPerfil(eq(3L), any())).thenReturn(usuario());

        mockMvc.perform(put("/api/usuarios/actualizar/3").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Valentina Soto\",\"telefono\":\"987541666\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/usuarios/recuperar-clave -> 200 (acepta 'identificar' como en la app)")
    void recuperarClave() throws Exception {
        UsuarioResponse r = usuario();
        r.setClaveTemporal("Kx7aP2m.48");
        when(services.recuperarClave(any())).thenReturn(r);

        mockMvc.perform(post("/api/usuarios/recuperar-clave").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identificar\":\"18.871.905-7\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claveTemporal").value("Kx7aP2m.48"));
    }
}

package com.backend.ms_roles.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.backend.ms_roles.dto.ActualizarPerfilRequest;
import com.backend.ms_roles.dto.CambiarClaveRequest;
import com.backend.ms_roles.dto.CrearUsuariosRequest;
import com.backend.ms_roles.dto.LoginRequest;
import com.backend.ms_roles.dto.LoginResponse;
import com.backend.ms_roles.dto.RecuperarClaveRequest;
import com.backend.ms_roles.dto.UsuarioResponse;
import com.backend.ms_roles.exception.CredencialesInvalidasException;
import com.backend.ms_roles.exception.DuplicadosException;
import com.backend.ms_roles.exception.NoEncontradoException;
import com.backend.ms_roles.exception.ReglasNegocioException;
import com.backend.ms_roles.model.Rol;
import com.backend.ms_roles.model.Usuario;
import com.backend.ms_roles.repository.RolRepository;
import com.backend.ms_roles.repository.UsuarioRepository;

// Tests unitarios del service: repositories y PasswordEncoder se simulan con Mockito (no necesita MySQL)
@ExtendWith(MockitoExtension.class)
class UsuarioServicesTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServices services;

    private final Rol rolCliente = new Rol(4L, "CLIENTE");
    private final Rol rolTecnico = new Rol(3L, "TECNICO");

    private Usuario usuario(boolean activo) {
        return Usuario.builder()
                .id(1L).nombre("Valentina Soto").correo("vsoto@induztek.cl").rut("18.871.905-7")
                .telefono("987541666").password("hash").activo(activo).cambiarClave(false).rol(rolTecnico)
                .build();
    }

    private CrearUsuariosRequest crearRequest(String rol, String giro, String contacto) {
        CrearUsuariosRequest r = new CrearUsuariosRequest();
        r.setRut("12345678-9");
        r.setNombre("Marcela Fuentes Rojas");
        r.setTelefono("+56912345678");
        r.setCorreo("mfuentes@empresa.cl");
        r.setRol(rol);
        r.setGiro(giro);
        r.setContactoCargo(contacto);
        return r;
    }

    private CambiarClaveRequest claveRequest(String actual, String nueva, String confirmar) {
        CambiarClaveRequest r = new CambiarClaveRequest();
        r.setClaveActual(actual);
        r.setClaveNueva(nueva);
        r.setConfirmarClave(confirmar);
        return r;
    }

    // ================= login =================

    @Test
    @DisplayName("login correcto devuelve id, nombre, rol y si debe cambiar la clave")
    void login_ok() {
        LoginRequest req = new LoginRequest();
        req.setCorreo("vsoto@induztek.cl");
        req.setPassword("Ude.-OloDE");
        when(usuarioRepository.findByCorreo("vsoto@induztek.cl")).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Ude.-OloDE", "hash")).thenReturn(true);

        LoginResponse r = services.login(req);

        assertEquals(1L, r.getIdUsuario());
        assertEquals("TECNICO", r.getRol());
        assertFalse(r.isDebeCambiarClave());
    }

    @Test
    @DisplayName("login con correo inexistente -> credenciales invalidas")
    void login_correoNoExiste() {
        LoginRequest req = new LoginRequest();
        req.setCorreo("nadie@induztek.cl");
        req.setPassword("Clave.123");
        when(usuarioRepository.findByCorreo("nadie@induztek.cl")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () -> services.login(req));
    }

    @Test
    @DisplayName("login con cuenta desactivada -> credenciales invalidas")
    void login_cuentaInactiva() {
        LoginRequest req = new LoginRequest();
        req.setCorreo("vsoto@induztek.cl");
        req.setPassword("Ude.-OloDE");
        when(usuarioRepository.findByCorreo("vsoto@induztek.cl")).thenReturn(Optional.of(usuario(false)));

        assertThrows(CredencialesInvalidasException.class, () -> services.login(req));
    }

    @Test
    @DisplayName("login con clave incorrecta -> credenciales invalidas")
    void login_claveIncorrecta() {
        LoginRequest req = new LoginRequest();
        req.setCorreo("vsoto@induztek.cl");
        req.setPassword("Mala.12345");
        when(usuarioRepository.findByCorreo("vsoto@induztek.cl")).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Mala.12345", "hash")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> services.login(req));
    }

    // ================= crear usuario =================

    @Test
    @DisplayName("crear cliente: guarda con clave temporal encriptada y debe cambiarla")
    void crear_cliente_ok() {
        when(usuarioRepository.existsByCorreo("mfuentes@empresa.cl")).thenReturn(false);
        when(usuarioRepository.existsByRut("12345678-9")).thenReturn(false);
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(7L);
            return u;
        });

        UsuarioResponse r = services.crearUsuario(crearRequest("CLIENTE", "Agroindustria del sur", "Jefa de Mantenimiento"));

        assertEquals(7L, r.getId());
        assertEquals("CLIENTE", r.getRol());
        assertNotNull(r.getClaveTemporal());
        assertEquals(10, r.getClaveTemporal().length());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("crear tecnico: no guarda giro ni contacto")
    void crear_tecnico_ok() {
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("TECNICO")).thenReturn(Optional.of(rolTecnico));
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        services.crearUsuario(crearRequest("tecnico", "giro que no aplica", "cargo"));

        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(u -> u.getGiro() == null && u.getContactoCargo() == null));
    }

    @Test
    @DisplayName("crear cliente sin giro -> regla de negocio")
    void crear_cliente_sinGiro() {
        ReglasNegocioException ex = assertThrows(ReglasNegocioException.class,
                () -> services.crearUsuario(crearRequest("CLIENTE", null, "Jefa")));
        assertEquals("El giro para Cliente es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("crear cliente con giro muy corto -> regla de negocio")
    void crear_cliente_giroCorto() {
        assertThrows(ReglasNegocioException.class,
                () -> services.crearUsuario(crearRequest("CLIENTE", "Agro", "Jefa")));
    }

    @Test
    @DisplayName("crear cliente sin contacto cargo -> regla de negocio")
    void crear_cliente_sinContacto() {
        assertThrows(ReglasNegocioException.class,
                () -> services.crearUsuario(crearRequest("CLIENTE", "Agroindustria del sur", " ")));
    }

    @Test
    @DisplayName("crear con correo repetido -> duplicado (409)")
    void crear_correoRepetido() {
        when(usuarioRepository.existsByCorreo("mfuentes@empresa.cl")).thenReturn(true);

        assertThrows(DuplicadosException.class,
                () -> services.crearUsuario(crearRequest("TECNICO", null, null)));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear con rut repetido -> duplicado (409)")
    void crear_rutRepetido() {
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut("12345678-9")).thenReturn(true);

        assertThrows(DuplicadosException.class,
                () -> services.crearUsuario(crearRequest("TECNICO", null, null)));
    }

    @Test
    @DisplayName("crear con rol que no existe -> no encontrado (404)")
    void crear_rolNoExiste() {
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("GERENTE")).thenReturn(Optional.empty());

        assertThrows(NoEncontradoException.class,
                () -> services.crearUsuario(crearRequest("GERENTE", null, null)));
    }

    // ================= listar, perfil y estado =================

    @Test
    @DisplayName("listar convierte cada usuario a UsuarioResponse")
    void listar() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario(true)));

        List<UsuarioResponse> lista = services.listarUsuarios();

        assertEquals(1, lista.size());
        assertEquals("vsoto@induztek.cl", lista.get(0).getCorreo());
        assertNull(lista.get(0).getClaveTemporal());
    }

    @Test
    @DisplayName("contar activas devuelve el campo contador")
    void contarActivas() {
        when(usuarioRepository.countByActivo(true)).thenReturn(6L);

        assertEquals(6L, services.contarActivas().getContador());
    }

    @Test
    @DisplayName("el perfil de un cliente trae giro y cargo (los muestra la pantalla Perfil)")
    void perfil_cliente_traeGiroYCargo() {
        Usuario cliente = Usuario.builder()
                .id(4L).nombre("Marcela Fuentes Rojas").correo("mfuentes@envpacifico.ejemplo").rut("14.226.791-8")
                .telefono("+56900000101").password("hash").activo(true).cambiarClave(false).rol(rolCliente)
                .giro("Agroindustria").contactoCargo("Jefa de Mantenimiento")
                .build();
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(cliente));

        UsuarioResponse r = services.obtenerPerfil(4L);

        assertEquals("Agroindustria", r.getGiro());
        assertEquals("Jefa de Mantenimiento", r.getContactoCargo());
    }

    @Test
    @DisplayName("obtener perfil existente")
    void perfil_ok() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));

        assertEquals("Valentina Soto", services.obtenerPerfil(1L).getNombre());
    }

    @Test
    @DisplayName("obtener perfil inexistente -> 404")
    void perfil_noExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoEncontradoException.class, () -> services.obtenerPerfil(99L));
    }

    @Test
    @DisplayName("cambiar estado invierte activo")
    void cambiarEstado_ok() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertFalse(services.cambiarEstado(1L).isActivo());
    }

    @Test
    @DisplayName("cambiar estado de usuario inexistente -> 404")
    void cambiarEstado_noExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoEncontradoException.class, () -> services.cambiarEstado(99L));
    }

    // ================= cambiar clave =================

    @Test
    @DisplayName("cambiar clave correcto: guarda la nueva y ya no debe cambiarla")
    void cambiarClave_ok() {
        Usuario u = usuario(true);
        u.setCambiarClave(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("Temp.12345", "hash")).thenReturn(true);
        when(passwordEncoder.matches("Nueva.123", "hash")).thenReturn(false);
        when(passwordEncoder.encode("Nueva.123")).thenReturn("hashNuevo");

        services.cambiarClave(1L, claveRequest("Temp.12345", "Nueva.123", "Nueva.123"));

        assertEquals("hashNuevo", u.getPassword());
        assertFalse(u.getCambiarClave());
        verify(usuarioRepository).save(u);
    }

    @Test
    @DisplayName("cambiar clave de usuario inexistente -> 404")
    void cambiarClave_noExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoEncontradoException.class,
                () -> services.cambiarClave(99L, claveRequest("Temp.12345", "Nueva.123", "Nueva.123")));
    }

    @Test
    @DisplayName("cambiar clave sin clave actual -> regla de negocio")
    void cambiarClave_sinClaveActual() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));

        assertThrows(ReglasNegocioException.class,
                () -> services.cambiarClave(1L, claveRequest(null, "Nueva.123", "Nueva.123")));
    }

    @Test
    @DisplayName("cambiar clave con clave actual incorrecta -> regla de negocio")
    void cambiarClave_actualIncorrecta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Mala.12345", "hash")).thenReturn(false);

        ReglasNegocioException ex = assertThrows(ReglasNegocioException.class,
                () -> services.cambiarClave(1L, claveRequest("Mala.12345", "Nueva.123", "Nueva.123")));
        assertEquals("La contraseña actual es incorrecta", ex.getMessage());
    }

    @Test
    @DisplayName("cambiar clave con confirmacion distinta -> regla de negocio")
    void cambiarClave_confirmacionDistinta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Temp.12345", "hash")).thenReturn(true);

        assertThrows(ReglasNegocioException.class,
                () -> services.cambiarClave(1L, claveRequest("Temp.12345", "Nueva.123", "Otra.1234")));
    }

    @Test
    @DisplayName("cambiar clave igual a la anterior -> regla de negocio")
    void cambiarClave_igualALaAnterior() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Temp.12345", "hash")).thenReturn(true);

        assertThrows(ReglasNegocioException.class,
                () -> services.cambiarClave(1L, claveRequest("Temp.12345", "Temp.12345", "Temp.12345")));
    }

    @Test
    @DisplayName("cambiar clave muy corta -> regla de negocio")
    void cambiarClave_largoInvalido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Temp.12345", "hash")).thenReturn(true);
        when(passwordEncoder.matches("Ab.1", "hash")).thenReturn(false);

        assertThrows(ReglasNegocioException.class,
                () -> services.cambiarClave(1L, claveRequest("Temp.12345", "Ab.1", "Ab.1")));
    }

    @Test
    @DisplayName("cambiar clave sin mayuscula -> regla de negocio")
    void cambiarClave_sinMayuscula() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Temp.12345", "hash")).thenReturn(true);
        when(passwordEncoder.matches("nueva.123", "hash")).thenReturn(false);

        assertThrows(ReglasNegocioException.class,
                () -> services.cambiarClave(1L, claveRequest("Temp.12345", "nueva.123", "nueva.123")));
    }

    @Test
    @DisplayName("cambiar clave sin punto ni guion -> regla de negocio")
    void cambiarClave_sinPuntoNiGuion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Temp.12345", "hash")).thenReturn(true);
        when(passwordEncoder.matches("Nueva1234", "hash")).thenReturn(false);

        assertThrows(ReglasNegocioException.class,
                () -> services.cambiarClave(1L, claveRequest("Temp.12345", "Nueva1234", "Nueva1234")));
    }

    // ================= actualizar perfil =================

    private ActualizarPerfilRequest perfilRequest(String correo) {
        ActualizarPerfilRequest r = new ActualizarPerfilRequest();
        r.setNombre(" Valentina Soto Perez ");
        r.setTelefono(" 987541666 ");
        r.setCorreo(correo);
        return r;
    }

    @Test
    @DisplayName("actualizar perfil con correo nuevo libre")
    void actualizar_correoNuevo() {
        Usuario u = usuario(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.existsByCorreo("nuevo@induztek.cl")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse r = services.actualizarPerfil(1L, perfilRequest("nuevo@induztek.cl"));

        assertEquals("nuevo@induztek.cl", r.getCorreo());
        assertEquals("Valentina Soto Perez", r.getNombre());
    }

    @Test
    @DisplayName("actualizar perfil sin correo mantiene el anterior")
    void actualizar_sinCorreo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("vsoto@induztek.cl", services.actualizarPerfil(1L, perfilRequest(null)).getCorreo());
    }

    @Test
    @DisplayName("actualizar perfil con el mismo correo no lo marca como repetido")
    void actualizar_mismoCorreo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        services.actualizarPerfil(1L, perfilRequest("VSOTO@induztek.cl"));

        verify(usuarioRepository, never()).existsByCorreo(anyString());
    }

    @Test
    @DisplayName("actualizar perfil con correo de otra cuenta -> duplicado (409)")
    void actualizar_correoDeOtro() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(true)));
        when(usuarioRepository.existsByCorreo("respinoza@induztek.cl")).thenReturn(true);

        assertThrows(DuplicadosException.class,
                () -> services.actualizarPerfil(1L, perfilRequest("respinoza@induztek.cl")));
    }

    @Test
    @DisplayName("actualizar perfil de usuario inexistente -> 404")
    void actualizar_noExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoEncontradoException.class, () -> services.actualizarPerfil(99L, perfilRequest(null)));
    }

    // ================= recuperar clave =================

    private RecuperarClaveRequest recuperar(String identificador) {
        RecuperarClaveRequest r = new RecuperarClaveRequest();
        r.setIdentificador(identificador);
        return r;
    }

    @Test
    @DisplayName("recuperar clave: genera temporal, la encripta y obliga a cambiarla")
    void recuperar_ok() {
        Usuario u = usuario(true);
        when(usuarioRepository.findByRutOrCorreo("18.871.905-7", "18.871.905-7")).thenReturn(Optional.of(u));
        when(passwordEncoder.encode(anyString())).thenReturn("hashTemporal");

        UsuarioResponse r = services.recuperarClave(recuperar("18.871.905-7"));

        assertNotNull(r.getClaveTemporal());
        assertEquals("hashTemporal", u.getPassword());
        assertTrue(u.getCambiarClave());
        verify(usuarioRepository).save(u);
    }

    @Test
    @DisplayName("recuperar clave de cuenta inexistente -> 404")
    void recuperar_noExiste() {
        when(usuarioRepository.findByRutOrCorreo("nadie", "nadie")).thenReturn(Optional.empty());

        assertThrows(NoEncontradoException.class, () -> services.recuperarClave(recuperar("nadie")));
    }

    @Test
    @DisplayName("recuperar clave de cuenta desactivada -> 404")
    void recuperar_inactiva() {
        when(usuarioRepository.findByRutOrCorreo("vsoto@induztek.cl", "vsoto@induztek.cl"))
                .thenReturn(Optional.of(usuario(false)));

        assertThrows(NoEncontradoException.class, () -> services.recuperarClave(recuperar("vsoto@induztek.cl")));
    }
}

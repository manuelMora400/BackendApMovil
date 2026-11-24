package com.example.ms_protocolo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ms_protocolo.client.InstrumentoClient;
import com.example.ms_protocolo.dto.ProtocoloResponseDTO;
import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.model.EnumResultado;
import com.example.ms_protocolo.repository.AislacionRepository;
import com.example.ms_protocolo.repository.ProtocoloRepository;
import com.example.ms_protocolo.service.ProtocoloService;

@WebMvcTest(ProtocoloController.class)
class ProtocoloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProtocoloService protocoloService;

    @MockitoBean
    private ProtocoloRepository protocoloRepository;

    @MockitoBean
    private AislacionRepository aislacionRepository;

    @MockitoBean
    private InstrumentoClient instrumentoClient;

    private static final String JSON_CREAR =
            "{\"temperatura\":21.5,\"humedad\":50.0,\"resultadoPrueba\":\"APROBADO\",\"idTecnico\":3,\"idInstrumento\":1}";

    private ProtocoloResponseDTO respuesta(Long id, EnumEstado estado) {
        return new ProtocoloResponseDTO(id, LocalDateTime.of(2026, 11, 20, 10, 30), 21.5, 50.0,
                -33.4489, -70.6693, estado, EnumResultado.APROBADO, 3L, 1L);
    }

    @Test
    @DisplayName("POST /api/protocolos valido -> 201 y queda PENDIENTE")
    void crear_valido_devuelve201() throws Exception {
        when(protocoloService.guardar(any())).thenReturn(respuesta(5L, EnumEstado.PENDIENTE));

        mockMvc.perform(post("/api/protocolos").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProtocolo").value(5))
                .andExpect(jsonPath("$.estadoProtocolo").value("PENDIENTE"))
                .andExpect(jsonPath("$.resultadoPrueba").value("APROBADO"));
    }

    @Test
    @DisplayName("POST sin resultado de la prueba -> 400 de validacion")
    void crear_sinResultado_devuelve400() throws Exception {
        mockMvc.perform(post("/api/protocolos").contentType(MediaType.APPLICATION_JSON).content("{\"temperatura\":21.5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validación"));

        verify(protocoloService, never()).guardar(any());
    }

    @Test
    @DisplayName("POST con instrumento vencido -> 400 con el mensaje del service")
    void crear_instrumentoVencido_devuelve400() throws Exception {
        when(protocoloService.guardar(any()))
                .thenThrow(new RuntimeException("El instrumento tiene la calibracion vencida, no se puede registrar el protocolo"));

        mockMvc.perform(post("/api/protocolos").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El instrumento tiene la calibracion vencida, no se puede registrar el protocolo"));
    }

    @Test
    @DisplayName("POST con ms-instrumentos caido -> 503")
    void crear_instrumentosCaido_devuelve503() throws Exception {
        when(protocoloService.guardar(any()))
                .thenThrow(new RuntimeException("No se pudo conectar con el servicio de instrumentos"));

        mockMvc.perform(post("/api/protocolos").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("GET /api/protocolos -> 200 con la lista")
    void listar_devuelve200() throws Exception {
        when(protocoloService.obtenerTodos()).thenReturn(List.of(respuesta(1L, EnumEstado.FORMALIZADO)));

        mockMvc.perform(get("/api/protocolos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoProtocolo").value("FORMALIZADO"));
    }

    @Test
    @DisplayName("GET /api/protocolos/{id} existente -> 200")
    void obtener_existente_devuelve200() throws Exception {
        when(protocoloService.obtenerPorId(1L)).thenReturn(Optional.of(respuesta(1L, EnumEstado.PENDIENTE)));

        mockMvc.perform(get("/api/protocolos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTecnico").value(3));
    }

    @Test
    @DisplayName("GET /api/protocolos/{id} inexistente -> 404")
    void obtener_noExiste_devuelve404() throws Exception {
        when(protocoloService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/protocolos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/protocolos/estado/PENDIENTE -> 200")
    void buscarPorEstado_devuelve200() throws Exception {
        when(protocoloService.buscarPorEstado(EnumEstado.PENDIENTE)).thenReturn(List.of(respuesta(2L, EnumEstado.PENDIENTE)));

        mockMvc.perform(get("/api/protocolos/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProtocolo").value(2));
    }

    @Test
    @DisplayName("GET /api/protocolos/estado/PENDIENTE/count -> 200 con el numero")
    void contarPorEstado_devuelve200() throws Exception {
        when(protocoloService.contarPorEstado(EnumEstado.PENDIENTE)).thenReturn(3L);

        mockMvc.perform(get("/api/protocolos/estado/PENDIENTE/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    @DisplayName("GET con un estado que no existe -> 400")
    void buscarPorEstado_invalido_devuelve400() throws Exception {
        mockMvc.perform(get("/api/protocolos/estado/INVENTADO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/protocolos/resultados -> 200 con los resultados posibles")
    void listarResultados_devuelve200() throws Exception {
        when(protocoloService.listarResultados()).thenReturn(List.of("APROBADO", "RECHAZADO", "OBSERVACION"));

        mockMvc.perform(get("/api/protocolos/resultados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2]").value("OBSERVACION"));
    }

    @Test
    @DisplayName("GET /api/protocolos/tecnico/{id} -> 200 con los protocolos del tecnico")
    void buscarPorTecnico_devuelve200() throws Exception {
        when(protocoloService.buscarPorTecnico(3L)).thenReturn(List.of(respuesta(1L, EnumEstado.PENDIENTE)));

        mockMvc.perform(get("/api/protocolos/tecnico/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTecnico").value(3));
    }

    @Test
    @DisplayName("PUT /api/protocolos/{id}/estado FORMALIZADO -> 200")
    void cambiarEstado_formalizado_devuelve200() throws Exception {
        when(protocoloService.cambiarEstado(eq(2L), eq(EnumEstado.FORMALIZADO)))
                .thenReturn(Optional.of(respuesta(2L, EnumEstado.FORMALIZADO)));

        mockMvc.perform(put("/api/protocolos/2/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estadoProtocolo\":\"FORMALIZADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoProtocolo").value("FORMALIZADO"));
    }

    @Test
    @DisplayName("PUT estado de un protocolo inexistente -> 404")
    void cambiarEstado_noExiste_devuelve404() throws Exception {
        when(protocoloService.cambiarEstado(eq(99L), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/protocolos/99/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estadoProtocolo\":\"FORMALIZADO\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT con un estado que no existe -> 400 cuerpo invalido")
    void cambiarEstado_estadoInvalido_devuelve400() throws Exception {
        mockMvc.perform(put("/api/protocolos/2/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estadoProtocolo\":\"APROBADO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cuerpo de la petición inválido"));
    }
}

package com.example.ms_protocolo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.example.ms_protocolo.dto.AislacionResponseDTO;
import com.example.ms_protocolo.model.EnumDesvanado;
import com.example.ms_protocolo.repository.AislacionRepository;
import com.example.ms_protocolo.repository.ProtocoloRepository;
import com.example.ms_protocolo.service.AislacionService;

@WebMvcTest(AislacionController.class)
class AislacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AislacionService aislacionService;

    @MockitoBean
    private ProtocoloRepository protocoloRepository;

    @MockitoBean
    private AislacionRepository aislacionRepository;

    @MockitoBean
    private InstrumentoClient instrumentoClient;

    private static final String JSON_CREAR =
            "{\"tag\":\"TR-01\",\"instalacion\":\"Planta Lampa\",\"enumDevanado\":\"AT_BT\","
            + "\"tension\":5.0,\"lectura1Min\":1200.0,\"lectura10Min\":2400.0,\"idProtocolo\":1}";

    private AislacionResponseDTO respuesta(Long id) {
        return new AislacionResponseDTO(id, "TR-01", "Planta Lampa", EnumDesvanado.AT_BT, 5.0, 1200.0, 2400.0, 1L);
    }

    @Test
    @DisplayName("POST /api/aislacion valido -> 201")
    void crear_valido_devuelve201() throws Exception {
        when(aislacionService.guardar(any())).thenReturn(Optional.of(respuesta(7L)));

        mockMvc.perform(post("/api/aislacion").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.idProtocolo").value(1));
    }

    @Test
    @DisplayName("POST de un protocolo que no existe -> 404")
    void crear_protocoloNoExiste_devuelve404() throws Exception {
        when(aislacionService.guardar(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/aislacion").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST sin TAG -> 400 de validacion")
    void crear_sinTag_devuelve400() throws Exception {
        String json = JSON_CREAR.replace("\"TR-01\"", "\"\"");

        mockMvc.perform(post("/api/aislacion").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validación"));

        verify(aislacionService, never()).guardar(any());
    }

    @Test
    @DisplayName("GET /api/aislacion -> 200 con la lista")
    void listar_devuelve200() throws Exception {
        when(aislacionService.obtenerTodos()).thenReturn(List.of(respuesta(1L), respuesta(2L)));

        mockMvc.perform(get("/api/aislacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/aislacion/{id} existente -> 200")
    void obtener_existente_devuelve200() throws Exception {
        when(aislacionService.obtenerPorId(1L)).thenReturn(Optional.of(respuesta(1L)));

        mockMvc.perform(get("/api/aislacion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tag").value("TR-01"));
    }

    @Test
    @DisplayName("GET /api/aislacion/{id} inexistente -> 404")
    void obtener_noExiste_devuelve404() throws Exception {
        when(aislacionService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/aislacion/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/aislacion/filtro por TAG e instalacion -> 200")
    void filtrar_devuelve200() throws Exception {
        when(aislacionService.filtrarPorTagEInstalacion("TR-01", "Planta Lampa")).thenReturn(List.of(respuesta(1L)));

        mockMvc.perform(get("/api/aislacion/filtro").param("tag", "TR-01").param("instalacion", "Planta Lampa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].instalacion").value("Planta Lampa"));
    }

    @Test
    @DisplayName("GET /api/aislacion/protocolo/{id} -> 200 con las pruebas del protocolo")
    void buscarPorProtocolo_devuelve200() throws Exception {
        when(aislacionService.buscarPorProtocolo(1L)).thenReturn(List.of(respuesta(1L)));

        mockMvc.perform(get("/api/aislacion/protocolo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProtocolo").value(1));
    }

    @Test
    @DisplayName("GET /api/aislacion/devanado/AT_BT -> 200")
    void buscarPorDevanado_devuelve200() throws Exception {
        when(aislacionService.buscarPorDevanado(EnumDesvanado.AT_BT)).thenReturn(List.of(respuesta(1L)));

        mockMvc.perform(get("/api/aislacion/devanado/AT_BT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enumDevanado").value("AT_BT"));
    }

    @Test
    @DisplayName("GET /api/aislacion/devanados -> 200 con la lista de devanados")
    void listarDevanados_devuelve200() throws Exception {
        when(aislacionService.listarDevanados()).thenReturn(List.of("AT_BT", "AT_T", "BT_T"));

        mockMvc.perform(get("/api/aislacion/devanados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("AT_BT"));
    }

    @Test
    @DisplayName("Un error inesperado -> 500 con mensaje generico")
    void errorInesperado_devuelve500() throws Exception {
        when(aislacionService.obtenerTodos()).thenAnswer(inv -> {
            throw new Exception("falla interna");
        });

        mockMvc.perform(get("/api/aislacion"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno del servidor"));
    }
}

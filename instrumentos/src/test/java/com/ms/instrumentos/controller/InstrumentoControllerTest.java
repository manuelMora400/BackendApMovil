package com.ms.instrumentos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ms.instrumentos.dto.ContadorResponse;
import com.ms.instrumentos.dto.InstrumentoResponse;
import com.ms.instrumentos.dto.VigenciaResponse;
import com.ms.instrumentos.exception.FechaInvalidaExeption;
import com.ms.instrumentos.exception.NumeroSerieDuplicadoException;
import com.ms.instrumentos.exception.RecursoNoEncontradoException;
import com.ms.instrumentos.repository.CalibracionRepository;
import com.ms.instrumentos.repository.InstrumentoRepository;
import com.ms.instrumentos.service.InstrumentoService;

@WebMvcTest(InstrumentoController.class)
class InstrumentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstrumentoService service;

    
    @MockitoBean
    private InstrumentoRepository instrumentoRepository;

    @MockitoBean
    private CalibracionRepository calibracionRepository;

    private static final String JSON_CREAR =
            "{\"nombre\":\"Megóhmetro 5 kV\",\"marca\":\"Megger\",\"modelo\":\"MIT525\","
            + "\"numeroSerie\":\"MIT-0045821\",\"fechaCalibracion\":\"2026-03-15\",\"fechaVencimiento\":\"2027-03-15\"}";

    private static final String JSON_RENOVAR =
            "{\"fechaCalibracion\":\"2026-11-23\",\"fechaVencimiento\":\"2027-11-23\"}";

    private InstrumentoResponse respuesta() {
        return InstrumentoResponse.builder().id(1L).nombre("Megóhmetro 5 kV").marca("Megger").modelo("MIT525")
                .numeroSerie("MIT-0045821").fechaCalibracion(LocalDate.of(2026, 3, 15))
                .fechaVencimiento(LocalDate.of(2027, 3, 15)).vigente(true).build();
    }

    @Test
    @DisplayName("GET /api/instrumentos -> 200 con la lista")
    void listar() throws Exception {
        when(service.listarInstrumentos()).thenReturn(List.of(respuesta()));

        mockMvc.perform(get("/api/instrumentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroSerie").value("MIT-0045821"))
                .andExpect(jsonPath("$[0].vigente").value(true));
    }

    @Test
    @DisplayName("GET /api/instrumentos/cantidad -> 200 con el campo contador")
    void contador() throws Exception {
        when(service.contador()).thenReturn(new ContadorResponse(5L));

        mockMvc.perform(get("/api/instrumentos/cantidad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contador").value(5));
    }

    @Test
    @DisplayName("POST valido -> 201")
    void crear_ok() throws Exception {
        when(service.registrar(any())).thenReturn(respuesta());

        mockMvc.perform(post("/api/instrumentos").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST sin nombre -> 400 con el mensaje de la validacion")
    void crear_sinNombre() throws Exception {
        String json = JSON_CREAR.replace("\"Megóhmetro 5 kV\"", "\"\"");

        mockMvc.perform(post("/api/instrumentos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
        verify(service, never()).registrar(any());
    }

    @Test
    @DisplayName("POST con serie repetida -> 409")
    void crear_serieRepetida() throws Exception {
        when(service.registrar(any())).thenThrow(new NumeroSerieDuplicadoException("El numero de serie ya existe"));

        mockMvc.perform(post("/api/instrumentos").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El numero de serie ya existe"));
    }

    @Test
    @DisplayName("POST con fechas invalidas -> 400")
    void crear_fechasInvalidas() throws Exception {
        when(service.registrar(any())).thenThrow(new FechaInvalidaExeption("Fechas invalidas"));

        mockMvc.perform(post("/api/instrumentos").contentType(MediaType.APPLICATION_JSON).content(JSON_CREAR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Fechas invalidas"));
    }

    @Test
    @DisplayName("GET /api/instrumentos/vigencia/{id} -> 200")
    void vigencia_ok() throws Exception {
        when(service.consultaVigencia(1L)).thenReturn(VigenciaResponse.builder()
                .idInstrumento(1L).vigente(false).fechaVencimiento(LocalDate.of(2026, 10, 10)).build());

        mockMvc.perform(get("/api/instrumentos/vigencia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vigente").value(false));
    }

    @Test
    @DisplayName("GET /api/instrumentos/vigencia/{id} inexistente -> 404")
    void vigencia_noExiste() throws Exception {
        when(service.consultaVigencia(99L)).thenThrow(new RecursoNoEncontradoException("No existe instrumentos para consultar"));

        mockMvc.perform(get("/api/instrumentos/vigencia/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe instrumentos para consultar"));
    }

    @Test
    @DisplayName("POST renovar calibracion -> 201")
    void renovar_ok() throws Exception {
        when(service.renovarCalibracion(eq("MIT-0045821"), any())).thenReturn(respuesta());

        mockMvc.perform(post("/api/instrumentos/serie/MIT-0045821/calibraciones")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_RENOVAR))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroSerie").value("MIT-0045821"));
    }

    @Test
    @DisplayName("POST renovar sin fecha de vencimiento -> 400")
    void renovar_sinFecha() throws Exception {
        mockMvc.perform(post("/api/instrumentos/serie/MIT-0045821/calibraciones")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"fechaCalibracion\":\"2026-11-23\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La fecha de vencimiento del instrumento es obligatoria"));
    }
}

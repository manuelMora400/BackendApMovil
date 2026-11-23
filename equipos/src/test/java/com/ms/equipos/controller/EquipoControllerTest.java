package com.ms.equipos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ms.equipos.dto.EquipoResponse;
import com.ms.equipos.exception.EquipoDuplicadoException;
import com.ms.equipos.exception.RecursoNoEncontradoException;
import com.ms.equipos.repository.EquipoRepository;
import com.ms.equipos.service.EquipoService;

@WebMvcTest(EquipoController.class)
class EquipoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipoService equipoService;

    @MockitoBean
    private EquipoRepository equipoRepository;

    private static final String JSON_VALIDO =
            "{\"tag\":\"TR-03\",\"tipo\":\"Transformador\",\"instalacion\":\"Planta Lampa\",\"idCliente\":4}";

    @Test
    @DisplayName("GET /api/equipos -> 200 con los campos que espera la app")
    void listar_devuelve200() throws Exception {
        when(equipoService.listarEquipos(null)).thenReturn(List.of(
                new EquipoResponse(1L, "TR-01", "Transformador", "Planta Lampa", 4L)));

        mockMvc.perform(get("/api/equipos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tag").value("TR-01"))
                .andExpect(jsonPath("$[0].tipo").value("Transformador"))
                .andExpect(jsonPath("$[0].instalacion").value("Planta Lampa"))
                .andExpect(jsonPath("$[0].idCliente").value(4));
    }

    @Test
    @DisplayName("POST valido -> 201")
    void crear_valido_devuelve201() throws Exception {
        when(equipoService.registrar(any())).thenReturn(
                new EquipoResponse(5L, "TR-03", "Transformador", "Planta Lampa", 4L));

        mockMvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.tag").value("TR-03"));
    }

    @Test
    @DisplayName("POST con TAG con espacio -> 400 con el mensaje de la validacion")
    void crear_tagInvalido_devuelve400() throws Exception {
        String json = "{\"tag\":\"TR 01\",\"tipo\":\"Transformador\",\"instalacion\":\"Planta Lampa\",\"idCliente\":4}";

        mockMvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El TAG solo puede tener letras, numeros y guion (ej: TR-01)"));

        verify(equipoService, never()).registrar(any());
    }

    @Test
    @DisplayName("POST sin idCliente -> 400")
    void crear_sinCliente_devuelve400() throws Exception {
        String json = "{\"tag\":\"TR-03\",\"tipo\":\"Transformador\",\"instalacion\":\"Planta Lampa\"}";

        mockMvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Seleccione el cliente dueño del equipo"));
    }

    @Test
    @DisplayName("POST con TAG repetido -> 409 con el mensaje del service")
    void crear_repetido_devuelve409() throws Exception {
        when(equipoService.registrar(any()))
                .thenThrow(new EquipoDuplicadoException("Ya existe el TAG TR-03 en Planta Lampa"));

        mockMvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe el TAG TR-03 en Planta Lampa"));
    }

    @Test
    @DisplayName("POST con JSON mal escrito (idCliente con letras) -> 400")
    void crear_jsonInvalido_devuelve400() throws Exception {
        String json = "{\"tag\":\"TR-03\",\"tipo\":\"Transformador\",\"instalacion\":\"Planta Lampa\",\"idCliente\":\"abc\"}";

        mockMvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El cuerpo de la peticion no es valido"));
    }

    @Test
    @DisplayName("GET /api/equipos/{id} existente -> 200")
    void obtener_existente_devuelve200() throws Exception {
        when(equipoService.obtenerPorId(1L)).thenReturn(
                new EquipoResponse(1L, "TR-01", "Transformador", "Planta Lampa", 4L));

        mockMvc.perform(get("/api/equipos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tag").value("TR-01"));
    }

    @Test
    @DisplayName("GET /api/equipos/{id} inexistente -> 404")
    void obtener_inexistente_devuelve404() throws Exception {
        when(equipoService.obtenerPorId(9999L)).thenThrow(new RecursoNoEncontradoException("No existe el equipo"));

        mockMvc.perform(get("/api/equipos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe el equipo"));
    }
}

package com.example.ms_protocolo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ms_protocolo.dto.VigenciaResponse;

import reactor.core.publisher.Mono;

class InstrumentoClientTest {

    private InstrumentoClient clienteQueResponde(HttpStatus status, String json) {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(peticion -> Mono.just(
                ClientResponse.create(status)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(json)
                        .build()));
        return new InstrumentoClient(builder, "http://instrumentos");
    }

    @Test
    @DisplayName("instrumento vigente -> devuelve vigente = true")
    void consultarVigencia_vigente() {
        InstrumentoClient cliente = clienteQueResponde(HttpStatus.OK,
                "{\"idInstrumento\":1,\"vigente\":true,\"fechaVencimiento\":\"2027-03-15\"}");

        VigenciaResponse vigencia = cliente.consultarVigencia(1L);

        assertEquals(1L, vigencia.getIdInstrumento());
        assertTrue(vigencia.isVigente());
    }

    @Test
    @DisplayName("instrumento vencido -> devuelve vigente = false")
    void consultarVigencia_vencido() {
        InstrumentoClient cliente = clienteQueResponde(HttpStatus.OK,
                "{\"idInstrumento\":2,\"vigente\":false,\"fechaVencimiento\":\"2026-10-10\"}");

        assertFalse(cliente.consultarVigencia(2L).isVigente());
    }

    @Test
    @DisplayName("ms-instrumentos responde 404 -> error de instrumento inexistente")
    void consultarVigencia_noExiste() {
        InstrumentoClient cliente = clienteQueResponde(HttpStatus.NOT_FOUND, "{\"message\":\"No existe\"}");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cliente.consultarVigencia(99L));

        assertEquals("No existe el instrumento con id 99", ex.getMessage());
    }

    @Test
    @DisplayName("ms-instrumentos con error -> mensaje de servicio no disponible")
    void consultarVigencia_servicioConError() {
        InstrumentoClient cliente = clienteQueResponde(HttpStatus.INTERNAL_SERVER_ERROR, "{}");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cliente.consultarVigencia(1L));

        assertEquals("No se pudo conectar con el servicio de instrumentos", ex.getMessage());
    }
}

package com.example.ms_protocolo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.ms_protocolo.dto.VigenciaResponse;

@Component
public class InstrumentoClient {

    private final WebClient webClient;

    public InstrumentoClient(WebClient.Builder builder,
            @Value("${servicios.instrumentos.url:http://instrumentos}") String urlBase) {
        this.webClient = builder.baseUrl(urlBase).build();
    }

    public VigenciaResponse consultarVigencia(Long idInstrumento) {
        try {
            return webClient.get()
                    .uri("/api/instrumentos/vigencia/{id}", idInstrumento)
                    .retrieve()
                    .bodyToMono(VigenciaResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("No existe el instrumento con id " + idInstrumento);
        } catch (WebClientException e) {
            throw new RuntimeException("No se pudo conectar con el servicio de instrumentos");
        }
    }
}

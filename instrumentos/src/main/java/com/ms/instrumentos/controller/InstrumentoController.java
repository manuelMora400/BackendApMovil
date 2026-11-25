package com.ms.instrumentos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ms.instrumentos.dto.ContadorResponse;
import com.ms.instrumentos.dto.CrearInstrumentoRequest;
import com.ms.instrumentos.dto.InstrumentoResponse;
import com.ms.instrumentos.dto.RenovarCalibracionRequest;
import com.ms.instrumentos.dto.VigenciaResponse;
import com.ms.instrumentos.service.InstrumentoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/instrumentos")
public class InstrumentoController {

    private final InstrumentoService service;

    public InstrumentoController(InstrumentoService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<InstrumentoResponse>> listarInstrumentos() {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarInstrumentos());
    }

    @GetMapping("/cantidad")
    public ResponseEntity<ContadorResponse> contador() {
        return ResponseEntity.status(HttpStatus.OK).body(service.contador());
    }

    @PostMapping()
    public ResponseEntity<InstrumentoResponse> crearInstrumento(@Valid @RequestBody CrearInstrumentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @GetMapping("/vigencia/{id}")
    public ResponseEntity<VigenciaResponse> vigencia(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.consultaVigencia(id));
    }

   
    @PostMapping("/serie/{numeroSerie}/calibraciones")
    public ResponseEntity<InstrumentoResponse> renovarCalibracion(
            @PathVariable String numeroSerie,
            @Valid @RequestBody RenovarCalibracionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.renovarCalibracion(numeroSerie, request));
    }
}

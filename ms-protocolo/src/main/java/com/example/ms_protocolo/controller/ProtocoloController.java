package com.example.ms_protocolo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ms_protocolo.dto.ProtocoloRequestDTO;
import com.example.ms_protocolo.dto.ProtocoloResponseDTO;
import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.service.ProtocoloService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/protocolos")
@RequiredArgsConstructor
public class ProtocoloController {

    private final ProtocoloService protocoloService;

    @PostMapping
    public ResponseEntity<ProtocoloResponseDTO> crear(@RequestBody ProtocoloRequestDTO request) {
        ProtocoloResponseDTO creado = protocoloService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ProtocoloResponseDTO>> listarTodos() {
        return ResponseEntity.ok(protocoloService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocoloResponseDTO> obtenerPorId(@PathVariable Long id) {
        return protocoloService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProtocoloResponseDTO>> buscarPorEstado(@PathVariable EnumEstado estado) {
        return ResponseEntity.ok(protocoloService.buscarPorEstado(estado));
    }

    @GetMapping("/estado/{estado}/count")
    public ResponseEntity<Long> contarPorEstado(@PathVariable EnumEstado estado) {
        return ResponseEntity.ok(protocoloService.contarPorEstado(estado));
    }
}

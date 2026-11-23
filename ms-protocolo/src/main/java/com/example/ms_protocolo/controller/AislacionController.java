package com.example.ms_protocolo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ms_protocolo.dto.AislacionRequestDTO;
import com.example.ms_protocolo.dto.AislacionResponseDTO;
import com.example.ms_protocolo.model.EnumDesvanado;
import com.example.ms_protocolo.service.AislacionService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/aislacion")
@RequiredArgsConstructor 
public class AislacionController {

    private final AislacionService aislacionService;

    @PostMapping
    public ResponseEntity<AislacionResponseDTO> crear(@RequestBody AislacionRequestDTO request) {
        return aislacionService.guardar(request)
                .map(creada -> ResponseEntity.status(HttpStatus.CREATED).body(creada))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AislacionResponseDTO>> listarTodos() {
        return ResponseEntity.ok(aislacionService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AislacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return aislacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<AislacionResponseDTO>> filtrar(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String instalacion) {
        return ResponseEntity.ok(aislacionService.filtrarPorTagEInstalacion(tag, instalacion));
    }

    @GetMapping("/protocolo/{idProtocolo}")
    public ResponseEntity<List<AislacionResponseDTO>> buscarPorProtocolo(@PathVariable Long idProtocolo) {
        return ResponseEntity.ok(aislacionService.buscarPorProtocolo(idProtocolo));
    }

    @GetMapping("/devanado/{devanado}")
    public ResponseEntity<List<AislacionResponseDTO>> buscarPorDevanado(@PathVariable EnumDesvanado devanado) {
        return ResponseEntity.ok(aislacionService.buscarPorDevanado(devanado));
    }
}
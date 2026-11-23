package com.ms.equipos.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ms.equipos.dto.CrearEquipoRequest;
import com.ms.equipos.dto.EquipoResponse;
import com.ms.equipos.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/equipos")
@Tag(name = "Equipos", description = "Equipos de los clientes (los registra el Jefe tecnico)")
public class EquipoController {

    private final EquipoService service;

    public EquipoController(EquipoService service) {
        this.service = service;
    }

    
    @Operation(summary = "Lista los equipos (opcional: solo los de un cliente)")
    @GetMapping()
    public ResponseEntity<List<EquipoResponse>> listarEquipos(
            @RequestParam(required = false) Long clienteId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarEquipos(clienteId));
    }

    
    @Operation(summary = "Registra un equipo de un cliente")
    @PostMapping()
    public ResponseEntity<EquipoResponse> crearEquipo(@Valid @RequestBody CrearEquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

   
    @Operation(summary = "Busca un equipo por id")
    @GetMapping("/{id}")
    public ResponseEntity<EquipoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.obtenerPorId(id));
    }
}

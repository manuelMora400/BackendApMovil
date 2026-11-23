package com.example.ms_protocolo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.ms_protocolo.dto.AislacionRequestDTO;
import com.example.ms_protocolo.dto.AislacionResponseDTO;
import com.example.ms_protocolo.model.Aislacion;
import com.example.ms_protocolo.model.EnumDesvanado;
import com.example.ms_protocolo.model.Protocolo;
import com.example.ms_protocolo.repository.AislacionRepository;
import com.example.ms_protocolo.repository.ProtocoloRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
@RequiredArgsConstructor 
public class AislacionService {

    private final AislacionRepository pruebaAislacionRepository;
    private final ProtocoloRepository protocoloRepository;

    @Transactional 
    public Optional<AislacionResponseDTO> guardar(AislacionRequestDTO dto) {
        log.info("Creando prueba de aislación con tag: {} e instalación: {}", dto.getTag(), dto.getInstalacion());

        Optional<Protocolo> protocoloOpt = protocoloRepository.findById(dto.getIdProtocolo());
        if (protocoloOpt.isEmpty()) {
            log.warn("No se puede guardar la prueba: protocolo no encontrado con id: {}", dto.getIdProtocolo());
            return Optional.empty();
        }

        Aislacion prueba = new Aislacion();
        prueba.setTag(dto.getTag());
        prueba.setInstalacion(dto.getInstalacion());
        prueba.setEnumDevanado(dto.getEnumDevanado());
        prueba.setTension(dto.getTension());
        prueba.setLectura1Min(dto.getLectura1Min());
        prueba.setLectura10Min(dto.getLectura10Min());
        prueba.setProtocolo(protocoloOpt.get());

        Aislacion guardada = pruebaAislacionRepository.save(prueba);

        log.info("Prueba de aislación guardada correctamente con id: {}", guardada.getId());

        return Optional.of(mapearAResponse(guardada));
    }

    public List<AislacionResponseDTO> obtenerTodos() {
        log.info("Listando todas las pruebas de aislación");

        List<AislacionResponseDTO> pruebas = pruebaAislacionRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());

        log.info("Total de pruebas de aislación encontradas: {}", pruebas.size());

        return pruebas;
    }

    public Optional<AislacionResponseDTO> obtenerPorId(Long id) {
        log.info("Buscando prueba de aislación con id: {}", id);

        Optional<AislacionResponseDTO> prueba = pruebaAislacionRepository.findById(id)
                .map(this::mapearAResponse);

        if (prueba.isEmpty()) {
            log.warn("Prueba de aislación no encontrada con id: {}", id);
        }

        return prueba;
    }

    public List<AislacionResponseDTO> filtrarPorTagEInstalacion(String tag, String instalacion) {
        log.info("Filtrando pruebas de aislación por tag: {} e instalación: {}", tag, instalacion);

        List<AislacionResponseDTO> pruebas = pruebaAislacionRepository
                .filtrarPorTagEInstalacion(tag, instalacion)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());

        log.info("Total de pruebas encontradas en filtro: {}", pruebas.size());

        return pruebas;
    }

    public List<AislacionResponseDTO> buscarPorProtocolo(Long idProtocolo) {
        log.info("Buscando pruebas de aislación para protocolo id: {}", idProtocolo);

        List<AislacionResponseDTO> pruebas = pruebaAislacionRepository.buscarPorProtocolo(idProtocolo)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());

        log.info("Total de pruebas encontradas para protocolo {}: {}", idProtocolo, pruebas.size());

        return pruebas;
    }

    public List<AislacionResponseDTO> buscarPorDevanado(EnumDesvanado devanado) {
        log.info("Buscando pruebas de aislación por devanado: {}", devanado);

        List<AislacionResponseDTO> pruebas = pruebaAislacionRepository.buscarPorDevanado(devanado)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());

        log.info("Total de pruebas encontradas con devanado {}: {}", devanado, pruebas.size());

        return pruebas;
    }

    private AislacionResponseDTO mapearAResponse(Aislacion prueba) {
        Long idProtocolo = prueba.getProtocolo() != null ? prueba.getProtocolo().getIdProtocolo() : null;
        return new AislacionResponseDTO(
                prueba.getId(),
                prueba.getTag(),
                prueba.getInstalacion(),
                prueba.getEnumDevanado(),
                prueba.getTension(),
                prueba.getLectura1Min(),
                prueba.getLectura10Min(),
                idProtocolo
        );
    }
}
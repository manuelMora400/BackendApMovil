package com.example.ms_protocolo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.ms_protocolo.dto.ProtocoloRequestDTO;
import com.example.ms_protocolo.dto.ProtocoloResponseDTO;
import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.model.Protocolo;
import com.example.ms_protocolo.repository.ProtocoloRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
@RequiredArgsConstructor 
public class ProtocoloService {

    private final ProtocoloRepository protocoloRepository;

    @Transactional 
    public ProtocoloResponseDTO guardar(ProtocoloRequestDTO dto) {
        log.info("Creando protocolo con temperatura: {} y humedad: {}", dto.getTemperatura(), dto.getHumedad());

        Protocolo protocolo = new Protocolo();
        protocolo.setFechaPrueba(dto.getFechaPrueba() != null ? dto.getFechaPrueba() : LocalDateTime.now());
        protocolo.setTemperatura(dto.getTemperatura());
        protocolo.setHumedad(dto.getHumedad());
        protocolo.setLatitud(dto.getLatitud());
        protocolo.setLongitud(dto.getLongitud());
        protocolo.setEstadoProtocolo(dto.getEstadoProtocolo() != null? dto.getEstadoProtocolo() : EnumEstado.PENDIENTE);

        Protocolo guardado = protocoloRepository.save(protocolo);

        log.info("Protocolo creado correctamente con id: {}", guardado.getIdProtocolo());

        return mapearAResponse(guardado);
    }

    public List<ProtocoloResponseDTO> obtenerTodos() {
        log.info("Listando todos los protocolos");

        List<ProtocoloResponseDTO> protocolos = protocoloRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());

        log.info("Total de protocolos encontrados: {}", protocolos.size());

        return protocolos;
    }

    public Optional<ProtocoloResponseDTO> obtenerPorId(Long id) {
        log.info("Buscando protocolo con id: {}", id);

        Optional<ProtocoloResponseDTO> protocolo = protocoloRepository.findById(id)
                .map(this::mapearAResponse);

        if (protocolo.isEmpty()) {
            log.warn("Protocolo no encontrado con id: {}", id);
        }
        return protocolo;
    }

    public List<ProtocoloResponseDTO> buscarPorEstado(EnumEstado estado) {
        log.info("Buscando protocolos por estado: {}", estado);

        List<ProtocoloResponseDTO> protocolos = protocoloRepository.buscarPorEstado(estado)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());

        log.info("Total de protocolos encontrados con estado {}: {}", estado, protocolos.size());

        return protocolos;
    }

    public long contarPorEstado(EnumEstado estado) {
        log.info("Contando protocolos con estado: {}", estado);
        return protocoloRepository.contarPorEstado(estado);
    }

    @Transactional
    public Optional<ProtocoloResponseDTO> cambiarEstado(
            Long id, EnumEstado nuevoEstado) {
        log.info("Cambiando estado del protocolo {} a {}", id, nuevoEstado);

        return protocoloRepository.findById(id).map(protocolo -> {
            protocolo.setEstadoProtocolo(nuevoEstado);
            return mapearAResponse(protocoloRepository.save(protocolo));
        });
    }


    private ProtocoloResponseDTO mapearAResponse(Protocolo protocolo) {
        return new ProtocoloResponseDTO(
                protocolo.getIdProtocolo(),
                protocolo.getFechaPrueba(),
                protocolo.getTemperatura(),
                protocolo.getHumedad(),
                protocolo.getLatitud(),
                protocolo.getLongitud(),
                protocolo.getEstadoProtocolo()
        );
    }
}

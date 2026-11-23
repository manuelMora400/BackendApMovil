package com.ms.equipos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ms.equipos.dto.CrearEquipoRequest;
import com.ms.equipos.dto.EquipoResponse;
import com.ms.equipos.exception.EquipoDuplicadoException;
import com.ms.equipos.exception.RecursoNoEncontradoException;
import com.ms.equipos.model.Equipo;
import com.ms.equipos.repository.EquipoRepository;

import jakarta.transaction.Transactional;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }


    private EquipoResponse mapear(Equipo equipo) {
        return EquipoResponse.builder()
                .id(equipo.getId())
                .tag(equipo.getTag())
                .tipo(equipo.getTipo())
                .instalacion(equipo.getInstalacion())
                .idCliente(equipo.getIdCliente())
                .build();
    }


    public List<EquipoResponse> listarEquipos(Long idCliente) {
        List<Equipo> equipos = (idCliente == null)
                ? equipoRepository.findAllByOrderByInstalacionAscTagAsc()
                : equipoRepository.findByIdClienteOrderByInstalacionAscTagAsc(idCliente);

        return equipos.stream().map(this::mapear).toList();
    }

    @Transactional
    public EquipoResponse registrar(CrearEquipoRequest request) {

      
        String tag = request.getTag().trim().toUpperCase();
        String tipo = request.getTipo().trim();
        String instalacion = request.getInstalacion().trim();

      
        if (equipoRepository.existsByTagIgnoreCaseAndInstalacionIgnoreCase(tag, instalacion)) {
            throw new EquipoDuplicadoException("Ya existe el TAG " + tag + " en " + instalacion);
        }

        Equipo equipo = Equipo.builder()
                .tag(tag)
                .tipo(tipo)
                .instalacion(instalacion)
                .idCliente(request.getIdCliente())
                .build();
        equipo = equipoRepository.save(equipo);

        return mapear(equipo);
    }

    //javier -> lo va a usar ms-protocolo (WebClient) para saber si el equipo existe y traer su TAG e instalacion
    public EquipoResponse obtenerPorId(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el equipo"));
        return mapear(equipo);
    }
}

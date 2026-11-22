package com.ms.instrumentos.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.ms.instrumentos.dto.CrearInstrumentoRequest;
import com.ms.instrumentos.dto.InstrumentoResponse;
import com.ms.instrumentos.model.Calibracion;
import com.ms.instrumentos.model.Instrumento;
import com.ms.instrumentos.repository.CalibracionRepository;
import com.ms.instrumentos.repository.InstrumentoRepository;

import jakarta.transaction.Transactional;

@Service
public class InstrumentoService {

    private final InstrumentoRepository instrumentoRepository;
    private final CalibracionRepository calibracionRepository;

    public InstrumentoService(InstrumentoRepository instrumentoRepository,CalibracionRepository calibracionRepository) {
        this.instrumentoRepository = instrumentoRepository;
        this.calibracionRepository = calibracionRepository;
    }

    @Transactional
    public InstrumentoResponse registrar(CrearInstrumentoRequest request) {

        if (!request.getFechaVencimiento().isAfter(request.getFechaCalibracion())) {
            throw new RuntimeException("La fecha de vencimiento no puede ser posterior a la fecha de calibracion");
        }
        if (instrumentoRepository.existsByNumeroSerie(request.getNumeroSerie())) {
            throw new RuntimeException("El numero de serie ya existe");
        }

        Instrumento instrumento = new Instrumento.builder()
                .nombre(request.getNombre())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .numeroSerie(request.getNumeroSerie())
                .build();
        instrumento = instrumentoRepository.save(instrumento);

        Calibracion calibracion = new Calibracion.builder()
                .fechaCalibracion(request.getFechaCalibracion())
                .fechaVencimiento(request.getFechaVencimiento())
                .instrumento(instrumento)
                .build();
        calibracion = calibracionRepository.save(calibracion);

        boolean estaVigente = !calibracion.getFechaVencimiento().isBefore(LocalDate.now());

        return InstrumentoResponse.builder()
                .id(instrumento.getId())
                .nombre(instrumento.getNombre())
                .marca(instrumento.getMarca())
                .modelo(instrumento.getModelo())
                .numeroSerie(instrumento.getNumeroSerie())
                .fechaCalibracion(calibracion.getFechaCalibracion())
                .fechaVencimiento(calibracion.getFechaCalibracion())
                .vigente(estaVigente)
                .build();
    }
}

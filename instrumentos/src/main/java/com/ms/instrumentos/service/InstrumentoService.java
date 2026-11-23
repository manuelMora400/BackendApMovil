package com.ms.instrumentos.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.ms.instrumentos.dto.ContadorResponse;
import com.ms.instrumentos.dto.CrearInstrumentoRequest;
import com.ms.instrumentos.dto.InstrumentoResponse;
import com.ms.instrumentos.dto.RenovarCalibracionRequest;
import com.ms.instrumentos.dto.VigenciaResponse;
import com.ms.instrumentos.exception.FechaInvalidaExeption;
import com.ms.instrumentos.exception.NumeroSerieDuplicadoException;
import com.ms.instrumentos.exception.RecursoNoEncontradoException;
import com.ms.instrumentos.model.Calibracion;
import com.ms.instrumentos.model.Instrumento;
import com.ms.instrumentos.repository.CalibracionRepository;
import com.ms.instrumentos.repository.InstrumentoRepository;

import jakarta.transaction.Transactional;

@Service
public class InstrumentoService {

    private final InstrumentoRepository instrumentoRepository;
    private final CalibracionRepository calibracionRepository;

    public InstrumentoService(InstrumentoRepository instrumentoRepository,
            CalibracionRepository calibracionRepository) {
        this.instrumentoRepository = instrumentoRepository;
        this.calibracionRepository = calibracionRepository;
    }

    public List<InstrumentoResponse> listarInstrumentos() {
        List<Instrumento> instrumento = instrumentoRepository.findAll();
        List<InstrumentoResponse> respuesta = new ArrayList<>();

        for (Instrumento instrumentos : instrumento) {
            
            Optional<Calibracion> ultimaCalibracion = calibracionRepository
                    .findTopByInstrumentoIdOrderByFechaVencimientoDesc(instrumentos.getId());

           if (ultimaCalibracion.isPresent()) {
                Calibracion calibracion = ultimaCalibracion.get();
                boolean vigente = !calibracion.getFechaVencimiento().isBefore(LocalDate.now());

            InstrumentoResponse response = InstrumentoResponse.builder()
                .id(instrumentos.getId())
                .nombre(instrumentos.getNombre())
                .marca(instrumentos.getMarca())
                .modelo(instrumentos.getModelo())
                .numeroSerie(instrumentos.getNumeroSerie())
                .fechaCalibracion(calibracion.getFechaCalibracion())
                .fechaVencimiento(calibracion.getFechaVencimiento())
                .vigente(vigente)
                .build();

                respuesta.add(response);
        }
      }
      return respuesta;
    }

    @Transactional
    public InstrumentoResponse registrar(CrearInstrumentoRequest request) {

        if (!request.getFechaVencimiento().isAfter(request.getFechaCalibracion())) {
            throw new FechaInvalidaExeption("La fecha de vencimientodebe ser posterior a la fecha de calibracion");
        }
        if (instrumentoRepository.existsByNumeroSerie(request.getNumeroSerie())) {
            throw new NumeroSerieDuplicadoException("El numero de serie ya existe");
        }

        Instrumento instrumento = Instrumento.builder()
                .nombre(request.getNombre())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .numeroSerie(request.getNumeroSerie())
                .build();
        instrumento = instrumentoRepository.save(instrumento);

        Calibracion calibracion = Calibracion.builder()
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
                .fechaVencimiento(calibracion.getFechaVencimiento())
                .vigente(estaVigente)
                .build();
    }

   
    @Transactional
    public InstrumentoResponse renovarCalibracion(String numeroSerie, RenovarCalibracionRequest request) {

        Instrumento instrumento = instrumentoRepository.findByNumeroSerie(numeroSerie.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un instrumento con ese numero de serie"));

   
        if (!request.getFechaVencimiento().isAfter(request.getFechaCalibracion())) {
            throw new FechaInvalidaExeption("La fecha de vencimiento debe ser posterior a la fecha de calibracion");
        }

        Calibracion calibracion = Calibracion.builder()
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
                .fechaVencimiento(calibracion.getFechaVencimiento())
                .vigente(estaVigente)
                .build();
    }

    public ContadorResponse contador() {
        return new ContadorResponse(instrumentoRepository.count());
    }

    public VigenciaResponse consultaVigencia(Long idInstrumento) {

        Optional<Instrumento> busqueda = instrumentoRepository.findById(idInstrumento);
        if (busqueda.isEmpty()) {
            throw new RecursoNoEncontradoException("No existe instrumentos para consultar");
        }
        Instrumento instrumento = busqueda.get();

        Optional<Calibracion> vigente = calibracionRepository
                .findTopByInstrumentoIdOrderByFechaVencimientoDesc(idInstrumento);

        if (vigente.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay instrumentos calibrados");
        }
        Calibracion calibracion = vigente.get();

        boolean estaVigente = !calibracion.getFechaVencimiento().isBefore(LocalDate.now());

        return VigenciaResponse.builder()
                .idInstrumento(instrumento.getId())
                .vigente(estaVigente)
                .fechaVencimiento(calibracion.getFechaVencimiento())
                .build();
    }
}

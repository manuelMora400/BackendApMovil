package com.ms.instrumentos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

// Tests unitarios del service: los repositories se simulan con Mockito (no necesita MySQL)
@ExtendWith(MockitoExtension.class)
class InstrumentoServiceTest {

    @Mock
    private InstrumentoRepository instrumentoRepository;

    @Mock
    private CalibracionRepository calibracionRepository;

    @InjectMocks
    private InstrumentoService instrumentoService;

    private final Instrumento megohmetro = Instrumento.builder()
            .id(1L).nombre("Megóhmetro 5 kV").marca("Megger").modelo("MIT525").numeroSerie("MIT-0045821").build();

    private CrearInstrumentoRequest crearRequest(LocalDate calibracion, LocalDate vencimiento) {
        CrearInstrumentoRequest r = new CrearInstrumentoRequest();
        r.setNombre("Megóhmetro 5 kV");
        r.setMarca("Megger");
        r.setModelo("MIT525");
        r.setNumeroSerie("MIT-0045821");
        r.setFechaCalibracion(calibracion);
        r.setFechaVencimiento(vencimiento);
        return r;
    }

    private RenovarCalibracionRequest renovarRequest(LocalDate calibracion, LocalDate vencimiento) {
        RenovarCalibracionRequest r = new RenovarCalibracionRequest();
        r.setFechaCalibracion(calibracion);
        r.setFechaVencimiento(vencimiento);
        return r;
    }

    // ---------- listar ----------

    @Test
    @DisplayName("listar calcula vigente con la ultima calibracion (vigente y vencido)")
    void listar_calculaVigencia() {
        Instrumento vencido = Instrumento.builder().id(2L).nombre("Micro-ohmímetro").marca("Megger")
                .modelo("DLRO10X").numeroSerie("DLR-10882").build();
        when(instrumentoRepository.findAll()).thenReturn(List.of(megohmetro, vencido));
        when(calibracionRepository.findTopByInstrumentoIdOrderByFechaVencimientoDesc(1L)).thenReturn(Optional.of(
                Calibracion.builder().fechaCalibracion(LocalDate.now()).fechaVencimiento(LocalDate.now().plusYears(1)).build()));
        when(calibracionRepository.findTopByInstrumentoIdOrderByFechaVencimientoDesc(2L)).thenReturn(Optional.of(
                Calibracion.builder().fechaCalibracion(LocalDate.now().minusYears(2)).fechaVencimiento(LocalDate.now().minusDays(1)).build()));

        List<InstrumentoResponse> lista = instrumentoService.listarInstrumentos();

        assertEquals(2, lista.size());
        assertTrue(lista.get(0).isVigente());
        assertFalse(lista.get(1).isVigente());
        assertEquals("DLR-10882", lista.get(1).getNumeroSerie());
    }

    @Test
    @DisplayName("listar no incluye instrumentos sin calibracion")
    void listar_omiteSinCalibracion() {
        when(instrumentoRepository.findAll()).thenReturn(List.of(megohmetro));
        when(calibracionRepository.findTopByInstrumentoIdOrderByFechaVencimientoDesc(1L)).thenReturn(Optional.empty());

        assertTrue(instrumentoService.listarInstrumentos().isEmpty());
    }

    // ---------- registrar ----------

    @Test
    @DisplayName("registrar guarda instrumento y calibracion")
    void registrar_ok() {
        when(instrumentoRepository.existsByNumeroSerie("MIT-0045821")).thenReturn(false);
        when(instrumentoRepository.save(any(Instrumento.class))).thenReturn(megohmetro);
        when(calibracionRepository.save(any(Calibracion.class))).thenAnswer(inv -> inv.getArgument(0));

        InstrumentoResponse r = instrumentoService.registrar(
                crearRequest(LocalDate.now(), LocalDate.now().plusYears(1)));

        assertEquals(1L, r.getId());
        assertTrue(r.isVigente());
        verify(calibracionRepository, times(1)).save(any(Calibracion.class));
    }

    @Test
    @DisplayName("registrar con vencimiento no posterior a la calibracion lanza error (400)")
    void registrar_fechaInvalida() {
        assertThrows(FechaInvalidaExeption.class, () -> instrumentoService.registrar(
                crearRequest(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1))));
        verify(instrumentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrar con numero de serie repetido lanza error (409)")
    void registrar_serieDuplicada() {
        when(instrumentoRepository.existsByNumeroSerie("MIT-0045821")).thenReturn(true);

        assertThrows(NumeroSerieDuplicadoException.class, () -> instrumentoService.registrar(
                crearRequest(LocalDate.now(), LocalDate.now().plusYears(1))));
        verify(instrumentoRepository, never()).save(any());
    }

    // ---------- contador y vigencia ----------

    @Test
    @DisplayName("contador devuelve la cantidad de instrumentos")
    void contador() {
        when(instrumentoRepository.count()).thenReturn(5L);

        assertEquals(5L, instrumentoService.contador().getContador());
    }

    @Test
    @DisplayName("vigencia de un instrumento vigente")
    void vigencia_ok() {
        when(instrumentoRepository.findById(1L)).thenReturn(Optional.of(megohmetro));
        when(calibracionRepository.findTopByInstrumentoIdOrderByFechaVencimientoDesc(1L)).thenReturn(Optional.of(
                Calibracion.builder().fechaCalibracion(LocalDate.now()).fechaVencimiento(LocalDate.now()).build()));

        VigenciaResponse v = instrumentoService.consultaVigencia(1L);

        assertEquals(1L, v.getIdInstrumento());
        assertTrue(v.getVigente()); // vence hoy: todavia esta vigente
    }

    @Test
    @DisplayName("vigencia de un instrumento que no existe lanza 404")
    void vigencia_noExiste() {
        when(instrumentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> instrumentoService.consultaVigencia(99L));
    }

    @Test
    @DisplayName("vigencia de un instrumento sin calibraciones lanza 404")
    void vigencia_sinCalibracion() {
        when(instrumentoRepository.findById(1L)).thenReturn(Optional.of(megohmetro));
        when(calibracionRepository.findTopByInstrumentoIdOrderByFechaVencimientoDesc(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> instrumentoService.consultaVigencia(1L));
    }

    // ---------- renovar calibracion ----------

    @Test
    @DisplayName("renovar agrega una calibracion nueva y queda vigente")
    void renovar_ok() {
        when(instrumentoRepository.findByNumeroSerie("MIT-0045821")).thenReturn(Optional.of(megohmetro));
        when(calibracionRepository.save(any(Calibracion.class))).thenAnswer(inv -> inv.getArgument(0));

        InstrumentoResponse r = instrumentoService.renovarCalibracion(" MIT-0045821 ",
                renovarRequest(LocalDate.now(), LocalDate.now().plusYears(1)));

        assertEquals("MIT-0045821", r.getNumeroSerie());
        assertTrue(r.isVigente());
        verify(calibracionRepository, times(1)).save(any(Calibracion.class));
    }

    @Test
    @DisplayName("renovar una serie que no existe lanza 404")
    void renovar_serieNoExiste() {
        when(instrumentoRepository.findByNumeroSerie("NO-EXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> instrumentoService.renovarCalibracion("NO-EXISTE",
                renovarRequest(LocalDate.now(), LocalDate.now().plusYears(1))));
        verify(calibracionRepository, never()).save(any());
    }

    @Test
    @DisplayName("renovar con vencimiento anterior a la calibracion lanza 400")
    void renovar_fechaInvalida() {
        when(instrumentoRepository.findByNumeroSerie("MIT-0045821")).thenReturn(Optional.of(megohmetro));

        assertThrows(FechaInvalidaExeption.class, () -> instrumentoService.renovarCalibracion("MIT-0045821",
                renovarRequest(LocalDate.of(2026, 11, 23), LocalDate.of(2026, 11, 1))));
        verify(calibracionRepository, never()).save(any());
    }
}

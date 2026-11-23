package com.ms.instrumentos.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ms.instrumentos.model.Calibracion;
import com.ms.instrumentos.model.Instrumento;
import com.ms.instrumentos.repository.CalibracionRepository;
import com.ms.instrumentos.repository.InstrumentoRepository;

// Prueba las semillas sin base de datos: los repositories son mocks
class CargaDatosConfigTest {

    @Test
    @DisplayName("con la tabla vacia se crean los 5 instrumentos con su calibracion")
    void cargaLosCincoInstrumentos() throws Exception {
        InstrumentoRepository instrumentos = mock(InstrumentoRepository.class);
        CalibracionRepository calibraciones = mock(CalibracionRepository.class);
        when(instrumentos.existsByNumeroSerie(anyString())).thenReturn(false);
        when(instrumentos.save(any(Instrumento.class))).thenAnswer(inv -> inv.getArgument(0));

        new CargaDatosConfig().initInstrumentos(instrumentos, calibraciones).run();

        verify(instrumentos, times(5)).save(any(Instrumento.class));
        verify(calibraciones, times(5)).save(any(Calibracion.class));
    }

    @Test
    @DisplayName("si ya existen no se duplican al reiniciar")
    void noDuplicaSiYaExisten() throws Exception {
        InstrumentoRepository instrumentos = mock(InstrumentoRepository.class);
        CalibracionRepository calibraciones = mock(CalibracionRepository.class);
        when(instrumentos.existsByNumeroSerie(anyString())).thenReturn(true);

        new CargaDatosConfig().initInstrumentos(instrumentos, calibraciones).run();

        verify(instrumentos, never()).save(any(Instrumento.class));
        verify(calibraciones, never()).save(any(Calibracion.class));
    }
}

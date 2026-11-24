package com.example.ms_protocolo.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ms_protocolo.model.Aislacion;
import com.example.ms_protocolo.model.Protocolo;
import com.example.ms_protocolo.repository.AislacionRepository;
import com.example.ms_protocolo.repository.ProtocoloRepository;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private ProtocoloRepository protocoloRepository;

    @Mock
    private AislacionRepository aislacionRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    @DisplayName("con la base vacia carga 4 protocolos y 5 pruebas de ejemplo")
    void run_baseVacia_cargaSemillas() {
        when(protocoloRepository.count()).thenReturn(0L);
        when(protocoloRepository.save(any(Protocolo.class))).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        verify(protocoloRepository, times(4)).save(any(Protocolo.class));
        verify(aislacionRepository, times(5)).save(any(Aislacion.class));
    }

    @Test
    @DisplayName("si ya hay protocolos no vuelve a cargar semillas")
    void run_conDatos_noCargaNada() {
        when(protocoloRepository.count()).thenReturn(4L);

        dataInitializer.run();

        verify(protocoloRepository, never()).save(any());
        verify(aislacionRepository, never()).save(any());
    }
}

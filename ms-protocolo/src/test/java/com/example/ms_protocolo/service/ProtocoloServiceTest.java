package com.example.ms_protocolo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ms_protocolo.client.InstrumentoClient;
import com.example.ms_protocolo.dto.ProtocoloRequestDTO;
import com.example.ms_protocolo.dto.ProtocoloResponseDTO;
import com.example.ms_protocolo.dto.VigenciaResponse;
import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.model.EnumResultado;
import com.example.ms_protocolo.model.Protocolo;
import com.example.ms_protocolo.repository.ProtocoloRepository;

@ExtendWith(MockitoExtension.class)
class ProtocoloServiceTest {

    @Mock
    private ProtocoloRepository protocoloRepository;

    @Mock
    private InstrumentoClient instrumentoClient;

    @InjectMocks
    private ProtocoloService protocoloService;

    private ProtocoloRequestDTO request(Long idInstrumento) {
        ProtocoloRequestDTO r = new ProtocoloRequestDTO();
        r.setTemperatura(21.5);
        r.setHumedad(50.0);
        r.setResultadoPrueba(EnumResultado.APROBADO);
        r.setIdTecnico(3L);
        r.setIdInstrumento(idInstrumento);
        return r;
    }

    private Protocolo protocolo(Long id, EnumEstado estado) {
        Protocolo p = new Protocolo();
        p.setIdProtocolo(id);
        p.setFechaPrueba(LocalDateTime.of(2026, 11, 20, 10, 30));
        p.setEstadoProtocolo(estado);
        p.setResultadoPrueba(EnumResultado.APROBADO);
        p.setIdTecnico(3L);
        p.setIdInstrumento(1L);
        return p;
    }

    private void guardarDevuelveConId(Long id) {
        when(protocoloRepository.save(any(Protocolo.class))).thenAnswer(inv -> {
            Protocolo p = inv.getArgument(0);
            p.setIdProtocolo(id);
            return p;
        });
    }

    @Test
    @DisplayName("guardar con instrumento vigente deja el protocolo PENDIENTE con su resultado")
    void guardar_instrumentoVigente_quedaPendiente() {
        when(instrumentoClient.consultarVigencia(1L)).thenReturn(new VigenciaResponse(1L, true));
        guardarDevuelveConId(10L);

        ProtocoloResponseDTO respuesta = protocoloService.guardar(request(1L));

        assertEquals(10L, respuesta.getIdProtocolo());
        assertEquals(EnumEstado.PENDIENTE, respuesta.getEstadoProtocolo());
        assertEquals(EnumResultado.APROBADO, respuesta.getResultadoPrueba());
        assertEquals(3L, respuesta.getIdTecnico());
        assertNotNull(respuesta.getFechaPrueba());
    }

    @Test
    @DisplayName("guardar con instrumento vencido lanza error y no guarda")
    void guardar_instrumentoVencido_lanzaError() {
        when(instrumentoClient.consultarVigencia(2L)).thenReturn(new VigenciaResponse(2L, false));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> protocoloService.guardar(request(2L)));

        assertTrue(ex.getMessage().contains("vencida"));
        verify(protocoloRepository, never()).save(any());
    }

    @Test
    @DisplayName("guardar sin instrumento no consulta a ms-instrumentos")
    void guardar_sinInstrumento_noConsultaVigencia() {
        guardarDevuelveConId(11L);

        protocoloService.guardar(request(null));

        verify(instrumentoClient, never()).consultarVigencia(any());
    }

    @Test
    @DisplayName("guardar respeta la fecha que viene en el request")
    void guardar_conFecha_laRespeta() {
        guardarDevuelveConId(12L);
        ProtocoloRequestDTO r = request(null);
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 1, 9, 0);
        r.setFechaPrueba(fecha);

        ProtocoloResponseDTO respuesta = protocoloService.guardar(r);

        assertEquals(fecha, respuesta.getFechaPrueba());
    }

    @Test
    @DisplayName("obtenerTodos devuelve todos los protocolos")
    void obtenerTodos_devuelveLista() {
        when(protocoloRepository.findAll()).thenReturn(List.of(protocolo(1L, EnumEstado.PENDIENTE), protocolo(2L, EnumEstado.FORMALIZADO)));

        assertEquals(2, protocoloService.obtenerTodos().size());
    }

    @Test
    @DisplayName("obtenerPorId existente devuelve el protocolo")
    void obtenerPorId_existente() {
        when(protocoloRepository.findById(1L)).thenReturn(Optional.of(protocolo(1L, EnumEstado.PENDIENTE)));

        Optional<ProtocoloResponseDTO> respuesta = protocoloService.obtenerPorId(1L);

        assertTrue(respuesta.isPresent());
        assertEquals(1L, respuesta.get().getIdProtocolo());
    }

    @Test
    @DisplayName("obtenerPorId inexistente devuelve vacio")
    void obtenerPorId_noExiste() {
        when(protocoloRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(protocoloService.obtenerPorId(99L).isEmpty());
    }

    @Test
    @DisplayName("buscarPorEstado filtra por el estado pedido")
    void buscarPorEstado_devuelveLosDelEstado() {
        when(protocoloRepository.buscarPorEstado(EnumEstado.PENDIENTE)).thenReturn(List.of(protocolo(2L, EnumEstado.PENDIENTE)));

        List<ProtocoloResponseDTO> respuesta = protocoloService.buscarPorEstado(EnumEstado.PENDIENTE);

        assertEquals(1, respuesta.size());
        assertEquals(EnumEstado.PENDIENTE, respuesta.get(0).getEstadoProtocolo());
    }

    @Test
    @DisplayName("contarPorEstado devuelve la cantidad del repositorio")
    void contarPorEstado_devuelveCantidad() {
        when(protocoloRepository.contarPorEstado(EnumEstado.PENDIENTE)).thenReturn(3L);

        assertEquals(3L, protocoloService.contarPorEstado(EnumEstado.PENDIENTE));
    }

    @Test
    @DisplayName("listarResultados entrega los resultados posibles de una prueba")
    void listarResultados_entregaLosTres() {
        assertEquals(List.of("APROBADO", "RECHAZADO", "OBSERVACION"), protocoloService.listarResultados());
    }

    @Test
    @DisplayName("buscarPorTecnico devuelve los protocolos del tecnico")
    void buscarPorTecnico_devuelveSusProtocolos() {
        when(protocoloRepository.findByIdTecnicoOrderByFechaPruebaDesc(3L)).thenReturn(List.of(protocolo(1L, EnumEstado.PENDIENTE)));

        List<ProtocoloResponseDTO> respuesta = protocoloService.buscarPorTecnico(3L);

        assertEquals(1, respuesta.size());
        assertEquals(3L, respuesta.get(0).getIdTecnico());
    }

    @Test
    @DisplayName("cambiarEstado de un protocolo existente lo deja FORMALIZADO")
    void cambiarEstado_existente_quedaFormalizado() {
        when(protocoloRepository.findById(2L)).thenReturn(Optional.of(protocolo(2L, EnumEstado.PENDIENTE)));
        when(protocoloRepository.save(any(Protocolo.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<ProtocoloResponseDTO> respuesta = protocoloService.cambiarEstado(2L, EnumEstado.FORMALIZADO);

        assertTrue(respuesta.isPresent());
        assertEquals(EnumEstado.FORMALIZADO, respuesta.get().getEstadoProtocolo());
        assertEquals(EnumResultado.APROBADO, respuesta.get().getResultadoPrueba());
    }

    @Test
    @DisplayName("cambiarEstado de un protocolo inexistente devuelve vacio y no guarda")
    void cambiarEstado_noExiste() {
        when(protocoloRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(protocoloService.cambiarEstado(99L, EnumEstado.FORMALIZADO).isEmpty());
        verify(protocoloRepository, never()).save(any());
    }
}

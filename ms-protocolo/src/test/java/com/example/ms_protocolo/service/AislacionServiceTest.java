package com.example.ms_protocolo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ms_protocolo.dto.AislacionRequestDTO;
import com.example.ms_protocolo.dto.AislacionResponseDTO;
import com.example.ms_protocolo.model.Aislacion;
import com.example.ms_protocolo.model.EnumDesvanado;
import com.example.ms_protocolo.model.Protocolo;
import com.example.ms_protocolo.repository.AislacionRepository;
import com.example.ms_protocolo.repository.ProtocoloRepository;

@ExtendWith(MockitoExtension.class)
class AislacionServiceTest {

    @Mock
    private AislacionRepository aislacionRepository;

    @Mock
    private ProtocoloRepository protocoloRepository;

    @InjectMocks
    private AislacionService aislacionService;

    private Protocolo protocolo(Long id) {
        Protocolo p = new Protocolo();
        p.setIdProtocolo(id);
        return p;
    }

    private Aislacion prueba(Long id, String tag, Protocolo protocolo) {
        Aislacion a = new Aislacion();
        a.setId(id);
        a.setTag(tag);
        a.setInstalacion("Planta Lampa");
        a.setEnumDevanado(EnumDesvanado.AT_BT);
        a.setTension(5.0);
        a.setLectura1Min(1200.0);
        a.setLectura10Min(2400.0);
        a.setProtocolo(protocolo);
        return a;
    }

    private AislacionRequestDTO request(Long idProtocolo) {
        return new AislacionRequestDTO("TR-01", "Planta Lampa", EnumDesvanado.AT_BT, 5.0, 1200.0, 2400.0, idProtocolo);
    }

    @Test
    @DisplayName("guardar una prueba de un protocolo existente la asocia al protocolo")
    void guardar_protocoloExiste_guardaLaPrueba() {
        when(protocoloRepository.findById(1L)).thenReturn(Optional.of(protocolo(1L)));
        when(aislacionRepository.save(any(Aislacion.class))).thenAnswer(inv -> {
            Aislacion a = inv.getArgument(0);
            a.setId(7L);
            return a;
        });

        Optional<AislacionResponseDTO> respuesta = aislacionService.guardar(request(1L));

        assertTrue(respuesta.isPresent());
        assertEquals(7L, respuesta.get().getId());
        assertEquals(1L, respuesta.get().getIdProtocolo());
        assertEquals("TR-01", respuesta.get().getTag());
    }

    @Test
    @DisplayName("guardar una prueba de un protocolo inexistente no guarda nada")
    void guardar_protocoloNoExiste_devuelveVacio() {
        when(protocoloRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(aislacionService.guardar(request(99L)).isEmpty());
        verify(aislacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerTodos devuelve todas las pruebas")
    void obtenerTodos_devuelveLista() {
        when(aislacionRepository.findAll()).thenReturn(List.of(prueba(1L, "TR-01", protocolo(1L)), prueba(2L, "TR-02", protocolo(2L))));

        assertEquals(2, aislacionService.obtenerTodos().size());
    }

    @Test
    @DisplayName("obtenerPorId existente devuelve la prueba")
    void obtenerPorId_existente() {
        when(aislacionRepository.findById(1L)).thenReturn(Optional.of(prueba(1L, "TR-01", protocolo(1L))));

        assertEquals("TR-01", aislacionService.obtenerPorId(1L).get().getTag());
    }

    @Test
    @DisplayName("obtenerPorId inexistente devuelve vacio")
    void obtenerPorId_noExiste() {
        when(aislacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(aislacionService.obtenerPorId(99L).isEmpty());
    }

    @Test
    @DisplayName("filtrar por TAG e instalacion usa el filtro del repositorio")
    void filtrar_devuelveLasCoincidencias() {
        when(aislacionRepository.filtrarPorTagEInstalacion("TR-01", "Planta Lampa"))
                .thenReturn(List.of(prueba(1L, "TR-01", protocolo(1L))));

        List<AislacionResponseDTO> respuesta = aislacionService.filtrarPorTagEInstalacion("TR-01", "Planta Lampa");

        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getIdProtocolo());
    }

    @Test
    @DisplayName("buscarPorProtocolo devuelve las pruebas del protocolo")
    void buscarPorProtocolo_devuelveSusPruebas() {
        when(aislacionRepository.buscarPorProtocolo(1L))
                .thenReturn(List.of(prueba(1L, "TR-01", protocolo(1L)), prueba(2L, "TR-01", protocolo(1L))));

        assertEquals(2, aislacionService.buscarPorProtocolo(1L).size());
    }

    @Test
    @DisplayName("buscarPorDevanado devuelve las pruebas de ese devanado")
    void buscarPorDevanado_devuelveLista() {
        when(aislacionRepository.buscarPorDevanado(EnumDesvanado.AT_BT)).thenReturn(List.of(prueba(1L, "TR-01", protocolo(1L))));

        assertEquals(EnumDesvanado.AT_BT, aislacionService.buscarPorDevanado(EnumDesvanado.AT_BT).get(0).getEnumDevanado());
    }

    @Test
    @DisplayName("listarDevanados entrega los 3 devanados del sistema")
    void listarDevanados_entregaLosTres() {
        assertEquals(List.of("AT_BT", "AT_T", "BT_T"), aislacionService.listarDevanados());
    }

    @Test
    @DisplayName("una prueba sin protocolo se muestra con idProtocolo nulo")
    void mapeo_sinProtocolo_idProtocoloNulo() {
        when(aislacionRepository.findById(3L)).thenReturn(Optional.of(prueba(3L, "TR-A", null)));

        assertNull(aislacionService.obtenerPorId(3L).get().getIdProtocolo());
    }
}

package com.ms.equipos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

import com.ms.equipos.dto.CrearEquipoRequest;
import com.ms.equipos.dto.EquipoResponse;
import com.ms.equipos.exception.EquipoDuplicadoException;
import com.ms.equipos.exception.RecursoNoEncontradoException;
import com.ms.equipos.model.Equipo;
import com.ms.equipos.repository.EquipoRepository;

// Tests unitarios del service: el repository se simula con Mockito (no necesita MySQL)
@ExtendWith(MockitoExtension.class)
class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @InjectMocks
    private EquipoService equipoService;

    private CrearEquipoRequest request(String tag, String tipo, String instalacion, Long idCliente) {
        CrearEquipoRequest r = new CrearEquipoRequest();
        r.setTag(tag);
        r.setTipo(tipo);
        r.setInstalacion(instalacion);
        r.setIdCliente(idCliente);
        return r;
    }

    @Test
    @DisplayName("registrar guarda el TAG en mayusculas y sin espacios a los lados")
    void registrar_guardaElTagEnMayusculas() {
        when(equipoRepository.existsByTagIgnoreCaseAndInstalacionIgnoreCase("TR-05", "Planta Lampa")).thenReturn(false);
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(inv -> {
            Equipo e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        EquipoResponse respuesta = equipoService.registrar(request(" tr-05 ", "Transformador", " Planta Lampa ", 4L));

        assertEquals(10L, respuesta.getId());
        assertEquals("TR-05", respuesta.getTag());
        assertEquals("Planta Lampa", respuesta.getInstalacion());
        assertEquals(4L, respuesta.getIdCliente());
        verify(equipoRepository, times(1)).save(any(Equipo.class));
    }

    @Test
    @DisplayName("registrar lanza 409 si el TAG ya existe en esa instalacion y no guarda nada")
    void registrar_lanzaErrorSiYaExiste() {
        when(equipoRepository.existsByTagIgnoreCaseAndInstalacionIgnoreCase("TR-01", "Planta Lampa")).thenReturn(true);

        EquipoDuplicadoException ex = assertThrows(EquipoDuplicadoException.class,
                () -> equipoService.registrar(request("TR-01", "Transformador", "Planta Lampa", 4L)));

        assertEquals("Ya existe el TAG TR-01 en Planta Lampa", ex.getMessage());
        verify(equipoRepository, never()).save(any());
    }

    @Test
    @DisplayName("listar sin cliente devuelve todos con sus campos")
    void listar_devuelveLosCampos() {
        Equipo equipo = Equipo.builder().id(1L).tag("TR-01").tipo("Transformador")
                .instalacion("Planta Lampa").idCliente(4L).build();
        when(equipoRepository.findAllByOrderByInstalacionAscTagAsc()).thenReturn(List.of(equipo));

        List<EquipoResponse> respuesta = equipoService.listarEquipos(null);

        assertEquals(1, respuesta.size());
        assertEquals("TR-01", respuesta.get(0).getTag());
        assertEquals("Planta Lampa", respuesta.get(0).getInstalacion());
        assertEquals(4L, respuesta.get(0).getIdCliente());
    }

    @Test
    @DisplayName("listar con cliente usa el filtro por idCliente (RN-18)")
    void listar_porCliente_usaElFiltro() {
        when(equipoRepository.findByIdClienteOrderByInstalacionAscTagAsc(6L)).thenReturn(List.of());

        equipoService.listarEquipos(6L);

        verify(equipoRepository, times(1)).findByIdClienteOrderByInstalacionAscTagAsc(6L);
        verify(equipoRepository, never()).findAllByOrderByInstalacionAscTagAsc();
    }

    @Test
    @DisplayName("obtenerPorId devuelve el equipo si existe")
    void obtenerPorId_devuelveElEquipo() {
        Equipo equipo = Equipo.builder().id(2L).tag("TR-A").tipo("Transformador")
                .instalacion("Planta Elevadora Sur").idCliente(5L).build();
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(equipo));

        EquipoResponse respuesta = equipoService.obtenerPorId(2L);

        assertEquals("TR-A", respuesta.getTag());
        assertEquals(5L, respuesta.getIdCliente());
    }

    @Test
    @DisplayName("obtenerPorId lanza 404 si no existe")
    void obtenerPorId_lanzaErrorSiNoExiste() {
        when(equipoRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> equipoService.obtenerPorId(9999L));
    }
}

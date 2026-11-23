package com.ms.equipos.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import com.ms.equipos.model.Equipo;
import com.ms.equipos.repository.EquipoRepository;

// Prueba las semillas sin base de datos: el repository es un mock
class CargaDatosConfigTest {

    @Test
    @DisplayName("con la tabla vacia se crean los 4 equipos semilla")
    void cargaLosCuatroEquipos() throws Exception {
        EquipoRepository repo = mock(EquipoRepository.class);
        when(repo.existsByTagIgnoreCaseAndInstalacionIgnoreCase(anyString(), anyString())).thenReturn(false);

        CommandLineRunner runner = new CargaDatosConfig().initEquipos(repo);
        runner.run();

        verify(repo, times(4)).save(any(Equipo.class));
    }

    @Test
    @DisplayName("si los equipos ya existen no se duplican al reiniciar")
    void noDuplicaSiYaExisten() throws Exception {
        EquipoRepository repo = mock(EquipoRepository.class);
        when(repo.existsByTagIgnoreCaseAndInstalacionIgnoreCase(anyString(), anyString())).thenReturn(true);

        new CargaDatosConfig().initEquipos(repo).run();

        verify(repo, never()).save(any(Equipo.class));
    }
}

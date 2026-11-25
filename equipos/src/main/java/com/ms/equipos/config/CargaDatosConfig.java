package com.ms.equipos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ms.equipos.model.Equipo;
import com.ms.equipos.repository.EquipoRepository;


@Configuration
public class CargaDatosConfig {

    private static final long MARCELA = 4L;   
    private static final long TOMAS = 5L;     
    private static final long PATRICIO = 6L;  

    @Bean
    CommandLineRunner initEquipos(EquipoRepository equipoRepository) {
        return args -> {
            crearSiNoExiste(equipoRepository, "TR-01", "Transformador", "Planta Lampa", MARCELA);
            crearSiNoExiste(equipoRepository, "TR-02", "Transformador", "Planta Lampa", MARCELA);
            crearSiNoExiste(equipoRepository, "TR-A", "Transformador", "Planta Elevadora Sur", TOMAS);
            crearSiNoExiste(equipoRepository, "TR-SS-01", "Transformador", "Faena Til Til", PATRICIO);
        };
    }

    private void crearSiNoExiste(
            EquipoRepository equipoRepository,
            String tag,
            String tipo,
            String instalacion,
            Long idCliente) {

        if (equipoRepository.existsByTagIgnoreCaseAndInstalacionIgnoreCase(tag, instalacion)) {
            return;
        }

        equipoRepository.save(Equipo.builder()
                .tag(tag)
                .tipo(tipo)
                .instalacion(instalacion)
                .idCliente(idCliente)
                .build());
    }
}

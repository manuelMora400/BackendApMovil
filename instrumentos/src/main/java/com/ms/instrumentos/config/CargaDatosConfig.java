package com.ms.instrumentos.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ms.instrumentos.model.Calibracion;
import com.ms.instrumentos.model.Instrumento;
import com.ms.instrumentos.repository.CalibracionRepository;
import com.ms.instrumentos.repository.InstrumentoRepository;


@Configuration
public class CargaDatosConfig {

    @Bean
    CommandLineRunner initInstrumentos(
            InstrumentoRepository instrumentoRepository,
            CalibracionRepository calibracionRepository) {

        return args -> {
            crearSiNoExiste(instrumentoRepository, calibracionRepository,
                    "Megóhmetro 5 kV", "Megger", "MIT525", "MIT-0045821",
                    LocalDate.of(2026, 3, 15), LocalDate.of(2027, 3, 15));

      
            crearSiNoExiste(instrumentoRepository, calibracionRepository,
                    "Megóhmetro 10 kV", "Megger", "MIT1025", "MIT-0071233",
                    LocalDate.of(2025, 10, 10), LocalDate.of(2026, 10, 10));

            crearSiNoExiste(instrumentoRepository, calibracionRepository,
                    "Medidor de relación (TTR)", "Megger", "TTR300", "TTR-30044",
                    LocalDate.of(2026, 6, 2), LocalDate.of(2027, 6, 2));

            crearSiNoExiste(instrumentoRepository, calibracionRepository,
                    "Micro-ohmímetro", "Megger", "DLRO10X", "DLR-10882",
                    LocalDate.of(2025, 8, 20), LocalDate.of(2026, 8, 20));

            crearSiNoExiste(instrumentoRepository, calibracionRepository,
                    "Equipo de inyección secundaria", "Omicron", "CMC 356", "CMC-HB411",
                    LocalDate.of(2026, 1, 5), LocalDate.of(2027, 1, 5));
        };
    }

    private void crearSiNoExiste(
            InstrumentoRepository instrumentoRepository,
            CalibracionRepository calibracionRepository,
            String nombre,
            String marca,
            String modelo,
            String numeroSerie,
            LocalDate fechaCalibracion,
            LocalDate fechaVencimiento) {

        if (instrumentoRepository.existsByNumeroSerie(numeroSerie)) {
            return;
        }

        Instrumento instrumento = instrumentoRepository.save(Instrumento.builder()
                .nombre(nombre)
                .marca(marca)
                .modelo(modelo)
                .numeroSerie(numeroSerie)
                .build());

        calibracionRepository.save(Calibracion.builder()
                .fechaCalibracion(fechaCalibracion)
                .fechaVencimiento(fechaVencimiento)
                .instrumento(instrumento)
                .build());
    }
}

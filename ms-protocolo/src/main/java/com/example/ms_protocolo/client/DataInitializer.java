package com.example.ms_protocolo.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.ms_protocolo.model.Aislacion;
import com.example.ms_protocolo.model.EnumDesvanado;
import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.model.Protocolo;
import com.example.ms_protocolo.repository.AislacionRepository;
import com.example.ms_protocolo.repository.ProtocoloRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor 
public class DataInitializer implements CommandLineRunner {

    private final ProtocoloRepository protocoloRepository;
    private final AislacionRepository pruebaAislacionRepository;

    @Override
    public void run(String... args) {

        if (protocoloRepository.count() > 0 && pruebaAislacionRepository.count() > 0) {
            log.info("Datos de protocolos y pruebas de aislación ya cargados. Se omite inicialización.");
            return;
        }

        log.info("Cargando datos iniciales de protocolos y pruebas de aislación...");

        Protocolo protocolo1 = new Protocolo();
        protocolo1.setFechaPrueba(LocalDateTime.now());
        protocolo1.setTemperatura(21.5);
        protocolo1.setHumedad(50.0);
        protocolo1.setLatitud(-33.4489);
        protocolo1.setLongitud(-70.6693);
        protocolo1.setEstadoProtocolo(EnumEstado.APROBADO);
        Protocolo protocolo1Guardado = protocoloRepository.save(protocolo1);

        Aislacion prueba1 = new Aislacion();
        prueba1.setId(null);
        prueba1.setTag("TAG-TR-01");
        prueba1.setInstalacion("Subestación Norte");
        prueba1.setEnumDevanado(EnumDesvanado.BT_T);
        prueba1.setTension(5.0);
        prueba1.setLectura1Min(1200.0);
        prueba1.setLectura10Min(2400.0);
        prueba1.setProtocolo(protocolo1Guardado);
        pruebaAislacionRepository.save(prueba1);

        log.info("Datos iniciales cargados correctamente.");
    }
}
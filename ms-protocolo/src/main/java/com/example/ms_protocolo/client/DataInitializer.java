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
        Protocolo protocoloGuardado = protocoloRepository.save(protocolo1);

        Aislacion prueba1 = new Aislacion();
        prueba1.setId(null);
        prueba1.setTag("TR-01");
        prueba1.setInstalacion("Planta Lampa");
        prueba1.setEnumDevanado(EnumDesvanado.AT_BT);
        prueba1.setTension(5.0);
        prueba1.setLectura1Min(1200.0);
        prueba1.setLectura10Min(2400.0);
        prueba1.setProtocolo(protocoloGuardado);
        pruebaAislacionRepository.save(prueba1);

        Aislacion prueba2 = new Aislacion();
        prueba2.setId(null);
        prueba2.setTag("TR-02");
        prueba2.setInstalacion("PPlanta Lampa");
        prueba2.setEnumDevanado(EnumDesvanado.AT_T);
        prueba2.setTension(5.0);
        prueba2.setLectura1Min(900.0);
        prueba2.setLectura10Min(1800.0);
        prueba2.setProtocolo(protocoloGuardado);
        pruebaAislacionRepository.save(prueba2);

        Aislacion prueba3 = new Aislacion();
        prueba3.setId(null);
        prueba3.setTag("TR-A");
        prueba3.setInstalacion("Planta Elevadora Sur");
        prueba3.setEnumDevanado(EnumDesvanado.AT_BT);
        prueba3.setTension(2.5);
        prueba3.setLectura1Min(1500.0);
        prueba1.setLectura10Min(3100.0);
        prueba1.setProtocolo(protocoloGuardado);
        pruebaAislacionRepository.save(prueba3);

        log.info("Datos iniciales cargados correctamente.");
    }
}
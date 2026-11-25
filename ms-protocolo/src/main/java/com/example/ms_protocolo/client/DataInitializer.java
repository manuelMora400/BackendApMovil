package com.example.ms_protocolo.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.ms_protocolo.model.Aislacion;
import com.example.ms_protocolo.model.EnumDesvanado;
import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.model.EnumResultado;
import com.example.ms_protocolo.model.Protocolo;
import com.example.ms_protocolo.repository.AislacionRepository;
import com.example.ms_protocolo.repository.ProtocoloRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final long TECNICO_VALENTINA = 3L;
    private static final long INSTRUMENTO_MIT_0045821 = 1L;
    private static final long INSTRUMENTO_TTR_30044 = 3L;

    private final ProtocoloRepository protocoloRepository;
    private final AislacionRepository pruebaAislacionRepository;

    @Override
    public void run(String... args) {

        if (protocoloRepository.count() > 0) {
            log.info("Datos de protocolos y pruebas de aislación ya cargados. Se omite inicialización.");
            return;
        }

        log.info("Cargando datos iniciales de protocolos y pruebas de aislación...");

        Protocolo formalizado = crearProtocolo(EnumEstado.FORMALIZADO, EnumResultado.APROBADO,
                INSTRUMENTO_MIT_0045821, 21.5, 50.0, 10);
        crearPrueba(formalizado, "TR-01", "Planta Lampa", EnumDesvanado.AT_BT, 5.0, 1200.0, 2400.0);
        crearPrueba(formalizado, "TR-01", "Planta Lampa", EnumDesvanado.AT_T, 5.0, 1100.0, 2300.0);

        Protocolo pendienteAprobado = crearProtocolo(EnumEstado.PENDIENTE, EnumResultado.APROBADO,
                INSTRUMENTO_TTR_30044, 19.0, 55.0, 3);
        crearPrueba(pendienteAprobado, "TR-02", "Planta Lampa", EnumDesvanado.AT_BT, 5.0, 900.0, 1800.0);

        Protocolo pendienteObservacion = crearProtocolo(EnumEstado.PENDIENTE, EnumResultado.OBSERVACION,
                INSTRUMENTO_MIT_0045821, 24.0, 40.0, 2);
        crearPrueba(pendienteObservacion, "TR-A", "Planta Elevadora Sur", EnumDesvanado.AT_BT, 2.5, 1500.0, 3100.0);

        Protocolo pendienteRechazado = crearProtocolo(EnumEstado.PENDIENTE, EnumResultado.RECHAZADO,
                INSTRUMENTO_TTR_30044, 26.5, 35.0, 1);
        crearPrueba(pendienteRechazado, "TR-SS-01", "Faena Til Til", EnumDesvanado.BT_T, 1.0, 80.0, 95.0);

        log.info("Datos iniciales cargados correctamente.");
    }

    private Protocolo crearProtocolo(EnumEstado estado, EnumResultado resultado, long idInstrumento,
            double temperatura, double humedad, int diasAtras) {
        Protocolo protocolo = new Protocolo();
        protocolo.setFechaPrueba(LocalDateTime.now().minusDays(diasAtras));
        protocolo.setTemperatura(temperatura);
        protocolo.setHumedad(humedad);
        protocolo.setLatitud(-33.4489);
        protocolo.setLongitud(-70.6693);
        protocolo.setEstadoProtocolo(estado);
        protocolo.setResultadoPrueba(resultado);
        protocolo.setIdTecnico(TECNICO_VALENTINA);
        protocolo.setIdInstrumento(idInstrumento);
        return protocoloRepository.save(protocolo);
    }

    private void crearPrueba(Protocolo protocolo, String tag, String instalacion, EnumDesvanado devanado,
            double tension, double lectura1Min, double lectura10Min) {
        Aislacion prueba = new Aislacion();
        prueba.setTag(tag);
        prueba.setInstalacion(instalacion);
        prueba.setEnumDevanado(devanado);
        prueba.setTension(tension);
        prueba.setLectura1Min(lectura1Min);
        prueba.setLectura10Min(lectura10Min);
        prueba.setProtocolo(protocolo);
        pruebaAislacionRepository.save(prueba);
    }
}

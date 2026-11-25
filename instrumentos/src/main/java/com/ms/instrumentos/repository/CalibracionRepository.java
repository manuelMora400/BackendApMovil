package com.ms.instrumentos.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ms.instrumentos.model.Calibracion;



public interface CalibracionRepository extends JpaRepository<Calibracion, Long>{

 Optional<Calibracion> findTopByInstrumentoIdOrderByFechaVencimientoDesc(Long instrumentoId);

}

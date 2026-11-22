package com.ms.instrumentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.instrumentos.model.Instrumento;
import java.util.Optional;


public interface InstrumentoRepository extends JpaRepository<Instrumento, Long> {
    Optional<Instrumento> findByNumeroSerie(String numeroSerie);

    boolean existsByNumeroSerie(String numeroSerie);

    Optional<Instrumento> findById(Long id);

}

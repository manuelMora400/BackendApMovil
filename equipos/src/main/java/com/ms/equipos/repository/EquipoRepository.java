package com.ms.equipos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.equipos.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

   
    boolean existsByTagIgnoreCaseAndInstalacionIgnoreCase(String tag, String instalacion);

  
    List<Equipo> findByIdClienteOrderByInstalacionAscTagAsc(Long idCliente);

    
    List<Equipo> findAllByOrderByInstalacionAscTagAsc();
}

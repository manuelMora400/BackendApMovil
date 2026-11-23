package com.example.ms_protocolo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ms_protocolo.model.EnumEstado;
import com.example.ms_protocolo.model.Protocolo;

@Repository
public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> {

    @Query ("SELECT p FROM Protocolo p WHERE p.estadoProtocolo = :estado ORDER BY p.fechaPrueba DESC")
    List<Protocolo> buscarPorEstado(@Param ("estado") EnumEstado estado);

    @Query("SELECT COUNT(p) FROM Protocolo p WHERE p.estadoProtocolo = :estado")
    long contarPorEstado(@Param("estado") EnumEstado estado);

    @Query("SELECT p FROM Protocolo p WHERE p.fechaPrueba BETWEEN :desde AND :hasta ORDER BY p.fechaPrueba DESC")
    List<Protocolo> buscarPorRangoFecha(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}

package com.example.ms_protocolo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ms_protocolo.model.Aislacion;
import com.example.ms_protocolo.model.EnumDesvanado;

public interface AislacionRepository extends JpaRepository<Aislacion, Long>{

    @Query("""
            SELECT pa FROM PruebaAislacion pa
            WHERE (:tag IS NULL OR LOWER(pa.tag) LIKE LOWER(CONCAT('%', :tag, '%')))
              AND (:instalacion IS NULL OR LOWER(pa.instalacion) LIKE LOWER(CONCAT('%', :instalacion, '%')))
            ORDER BY pa.protocolo.idProtocolo DESC
            """)

    List<Aislacion> filtrarPorTagEInstalacion(
            @Param("tag") String tag,
            @Param("instalacion") String instalacion
    );

    @Query("SELECT pa FROM PruebaAislacion pa WHERE pa.protocolo.idProtocolo = :idProtocolo")
    List<Aislacion> buscarPorProtocolo(@Param("idProtocolo") Long idProtocolo);

    @Query("SELECT pa FROM PruebaAislacion pa WHERE pa.enumDevanado = :devanado")
    List<Aislacion> buscarPorDevanado(@Param ("devanado") EnumDesvanado devanado);

}
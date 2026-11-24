package com.example.ms_protocolo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor 
@Entity
@Table(name = "protocolo")
public class Protocolo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProtocolo;

    @Column(name = "fecha_prueba")
    private LocalDateTime fechaPrueba;

    @Column(name = "temperatura")
    private Double temperatura;

    @Column(name = "humedad")
    private Double humedad;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Enumerated (EnumType.STRING)
    @Column(name = "estado_protocolo", columnDefinition = "varchar(20)")
    private EnumEstado estadoProtocolo;

    @Enumerated (EnumType.STRING)
    @Column(name = "resultado_prueba", columnDefinition = "varchar(20)")
    private EnumResultado resultadoPrueba;

    @Column(name = "id_tecnico")
    private Long idTecnico;

    @Column(name = "id_instrumento")
    private Long idInstrumento;
}

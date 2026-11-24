package com.example.ms_protocolo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity 
@Table(name = "Aislacion")
public class Aislacion {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "tag")
    private String tag;

    @Column (name = "instalacion")
    private String instalacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "enumDevanado")
    private EnumDesvanado enumDevanado;

    @Column (name = "tension")
    private Double tension;

    @Column (name = "lectura1Min")
    private Double lectura1Min;

    @Column (name = "lectura10Min")
    private Double lectura10Min;

    @ManyToOne
    @JoinColumn(name = "idProtocolo", nullable = false)
    private Protocolo protocolo;

}

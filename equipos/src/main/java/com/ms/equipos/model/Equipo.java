package com.ms.equipos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "equipos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tag", "instalacion"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String tag;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(nullable = false, length = 80)
    private String instalacion;


    @Column(nullable = false)
    private Long idCliente;
}

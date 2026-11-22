package com.ms.instrumentos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instrumentos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Instrumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del instrumento es obligatorio")
    @Column(nullable = false, length = 30)
    private String nombre;

    @NotBlank(message = "La marca del instrumento  es obligatorio")
    @Column(nullable = false, length = 30)
    private String marca;

    @NotBlank(message = "La marca del instrumento es obligatoria")
    @Column(nullable = false, length = 40)
    private String modelo;

    @NotBlank(message = "El numero de serie es obligatorio")
    @Column(nullable = false, unique = true, length = 25)
    private String numeroSerie;
}

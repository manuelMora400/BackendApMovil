package com.backend.ms_roles.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Rol {
    
    @Schema(description = "Identificador unico de cada rol", example = "1")
    @Id
    private Long id_rol;

    @Schema(description = "Identifica al rol por nombre", example = "CLIENTE")
    @Column(nullable = false, unique = true, length = 40)
    private String nombre;
}

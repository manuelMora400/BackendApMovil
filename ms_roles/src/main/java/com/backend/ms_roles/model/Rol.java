package com.backend.ms_roles.model;

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

    @Id 
    private Long id_rol;

    @Column(nullable = false, unique = true, length = 40)
    private String nombre;
}

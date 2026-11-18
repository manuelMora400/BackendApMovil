
package com.backend.ms_roles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, unique = true)
    private String rut;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String password;

    // nulls todo roles menos cliente
    @Column(nullable = true)
    private String giro;

    // nulls todo roles menos cliente
    @Column(nullable = true)
    private String contactoCargo;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private boolean cambiarClave = true;

    @ManyToOne
    private Rol rol;
}

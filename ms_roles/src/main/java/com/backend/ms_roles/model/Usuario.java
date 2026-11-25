
package com.backend.ms_roles.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Schema(description = "Identificador unico del usuario", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre de los usuarios", example = "Manuel Mora")
    @Column(nullable = false)
    private String nombre;

    @Schema(description = "Correo electronico unico del usuario", example = "manuel@gmail.com")
    @Column(nullable = false, unique = true)
    private String correo;

    @Schema(description = "Rut que identifica la entidad unica del usuario", example = "22256967-2")
    @Column(nullable = false, unique = true)
    private String rut;

    @Schema(description = "Numero telefono para el contacto del usuario", example = "+56931214567")
    @Column(nullable = false)
    private String telefono;

    @Schema(description = "Clave perteneciente a su credencial para iniciar sesion", example = "*********")
    @Column(nullable = false)
    private String password;

    @Schema(description = "Exclusivo para cliente, identifica empresa donde pertenece", example = "Empresa colina")
    @Column(nullable = true)
    private String giro;

    @Schema(description = "Exclusivo para cliente, identifica el cargo que aborda el cliente", example = "Bodegero")
    @Column(nullable = true)
    private String contactoCargo;

    @Schema(description = "Señala el estado de las cuentas de los usuarios", example = "ACTIVADA")
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Schema(description = "Permite a los usuarios cambiar su clave para inicio de sesion", example = "Cambie su contraseña")
    @Builder.Default
    @Column(nullable = false)
    private Boolean cambiarClave = true;
    
    @Schema(description = "Permite unir rol asociado a un usuario", example = "JEFE_TECNICO")
    @ManyToOne
    private Rol rol;
}

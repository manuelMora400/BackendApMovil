package com.backend.ms_roles.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.ms_roles.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByCorreo(String correo);

    List<Usuario> findByRutOrCorreo(String rut, String correo);

    boolean existsByRut(String rut);

    boolean existsByCorreo(String correo);

    long countByActivo(boolean activo);

}
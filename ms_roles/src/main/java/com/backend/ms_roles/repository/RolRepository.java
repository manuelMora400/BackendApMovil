package com.backend.ms_roles.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.ms_roles.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

    List<Rol> findByNombre(String nombre);

}

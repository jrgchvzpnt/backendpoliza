package com.coppel.proyecto.poliza.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coppel.proyecto.poliza.models.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

}

package com.coppel.proyecto.poliza.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coppel.proyecto.poliza.models.Poliza;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    @Query("FROM Poliza c WHERE c.empleadoGenero.idEmpleado = :idEmpleado OR LOWER(c.empleadoGenero.nombre) LIKE %:fullname% OR LOWER(c.empleadoGenero.apellido) LIKE %:fullname%")
    List<Poliza> search(@Param("idEmpleado") Long dni, @Param("fullname") String fullname);

    @Query("FROM Poliza c WHERE c.fecha BETWEEN :date1 AND :date2")
    List<Poliza> searchByDates(@Param("date1") String date1, @Param("date2") String date2);

}

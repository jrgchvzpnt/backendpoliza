package com.coppel.proyecto.poliza.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Empleado {

    @Id
    private Long idEmpleado;
    private String nombre;
    private String apellido;
    private String puesto;

}

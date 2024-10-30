package com.coppel.proyecto.poliza.models;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity

public class Poliza {

    @Id
    private Long idPoliza;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleadoGenero;

    @ManyToOne
    @JoinColumn(name = "sku")
    private Inventario inventario;

    private Integer cantidad;
    private Date fecha;
}

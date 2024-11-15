package com.coppel.proyecto.poliza.models;

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
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleadoGenero;

    @ManyToOne
    @JoinColumn(name = "sku", nullable = false)
    private Inventario inventario;

    @Column(length = 2, nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private String fecha;
}

package com.coppel.proyecto.poliza.models;

import java.time.LocalDateTime;

import org.antlr.v4.runtime.misc.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PolizaDTO {

    private Long idPoliza;

    @SuppressWarnings("deprecation")
    @NotNull
    private Empleado empleado;

    @SuppressWarnings("deprecation")
    @NotNull
    private Inventario inventario;

    private Integer cantidad;

    private String fecha;

}

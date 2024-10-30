package com.coppel.proyecto.poliza.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity

public class Inventario {
    @Id
    private Long sku;
    private String nombre;
    private Integer cantidad;
}

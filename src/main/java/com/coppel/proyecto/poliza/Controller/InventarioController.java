package com.coppel.proyecto.poliza.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.coppel.proyecto.poliza.Repository.InventarioRepository;
import com.coppel.proyecto.poliza.models.Inventario;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioRepository inventarioRepository;

    @GetMapping
    public ResponseEntity<?> getAllinventarios() {
        List<Inventario> inventario = inventarioRepository.findAll();
        if (!inventario.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Inventario", inventario)));
        } else {
            return ResponseEntity.status(404).body(
                    Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                            Map.of("Mensaje", "SKU no encontrado.")));
        }
    }

    @GetMapping("/{sku}")
    public ResponseEntity<?> getInventario(@PathVariable Long sku) {
        Optional<Inventario> inventarioOpt = inventarioRepository.findById(sku);
        if (inventarioOpt.isPresent()) {
            Inventario inventario = inventarioOpt.get();
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Inventario", inventario)));
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "SKU no encontrado.")));
        }
    }

    @PostMapping
    public ResponseEntity<?> createInventario(@RequestBody Inventario inventario) {
        if (inventarioRepository.existsById(inventario.getSku())) {
            return ResponseEntity.status(400)
                    .body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "SKU ya existe.")));
        }
        inventarioRepository.save(inventario);
        return ResponseEntity.ok(Map.of(
                "Meta", Map.of("Status", "OK"),
                "Data", Map.of("Inventario", inventario)));
    }

    @PutMapping("/{sku}")
    public ResponseEntity<?> updateInventario(@PathVariable Long sku, @RequestBody Inventario inventarioDetails) {
        return inventarioRepository.findById(sku).map(inventario -> {
            inventario.setNombre(inventarioDetails.getNombre());
            inventario.setCantidad(inventarioDetails.getCantidad());
            inventarioRepository.save(inventario);
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Inventario", inventario)));
        }).orElseGet(() -> ResponseEntity.status(404)
                .body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "SKU no encontrado."))));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<?> deleteInventario(@PathVariable Long sku) {
        if (inventarioRepository.existsById(sku)) {
            inventarioRepository.deleteById(sku);
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Mensaje", "Artículo de inventario eliminado correctamente.")));
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "SKU no encontrado.")));
        }
    }

}

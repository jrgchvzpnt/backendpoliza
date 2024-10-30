package com.coppel.proyecto.poliza.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.coppel.proyecto.poliza.Repository.EmpleadoRepository;
import com.coppel.proyecto.poliza.Repository.InventarioRepository;
import com.coppel.proyecto.poliza.Repository.PolizaRepository;
import com.coppel.proyecto.poliza.models.Inventario;
import com.coppel.proyecto.poliza.models.Poliza;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {

    @Autowired
    private PolizaRepository polizaRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private InventarioRepository inventarioRepository;

    // Endpoint para consultar una póliza
    @GetMapping("/{id}")
    public ResponseEntity<?> getPoliza(@PathVariable Long id) {
        Optional<Poliza> polizaOpt = polizaRepository.findById(id);
        if (polizaOpt.isPresent()) {
            Poliza poliza = polizaOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("Meta", Map.of("Status", "OK"));
            response.put("Data", Map.of(
                    "Poliza", Map.of("IDPoliza", poliza.getIdPoliza(), "Cantidad", poliza.getCantidad()),
                    "Empleado",
                    Map.of("Nombre", poliza.getEmpleadoGenero().getNombre(), "Apellido",
                            poliza.getEmpleadoGenero().getApellido()),
                    "DetalleArticulo",
                    Map.of("SKU", poliza.getInventario().getSku(), "Nombre", poliza.getInventario().getNombre())));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                    Map.of("Mensaje", "Ha ocurrido un error al consultar la póliza.")));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPoliza(@RequestBody Poliza poliza) {

        try {

            if (polizaRepository.existsById(poliza.getIdPoliza())) {
                return ResponseEntity.status(400).body(
                        Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                                Map.of("Mensaje", "ID de póliza ya existe.")));
            }

            if (!empleadoRepository.existsById(poliza.getEmpleadoGenero().getIdEmpleado())) {
                return ResponseEntity.status(400).body(
                        Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "Empleado no existe.")));
            }

            Optional<Inventario> inventarioOpt = inventarioRepository.findById(poliza.getInventario().getSku());
            if (inventarioOpt.isEmpty() || inventarioOpt.get().getCantidad() < poliza.getCantidad()) {
                return ResponseEntity.status(400).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                        Map.of("Mensaje", "SKU no existe o no hay suficiente cantidad en inventario.")));
            }

            // Reducir la cantidad en inventario
            Inventario inventario = inventarioOpt.get();
            inventario.setCantidad(inventario.getCantidad() - poliza.getCantidad());
            inventarioRepository.save(inventario);

            // Guardar la nueva póliza
            polizaRepository.save(poliza);

            Optional<Poliza> polizaOpt = polizaRepository.findById(poliza.getIdPoliza());
            Poliza polizaConsultar = polizaOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("Meta", Map.of("Status", "OK"));
            response.put("Data", Map.of(
                    "Poliza",
                    Map.of("IDPoliza", polizaConsultar.getIdPoliza(), "Cantidad", polizaConsultar.getCantidad()),
                    "Empleado",
                    Map.of("Nombre", polizaConsultar.getEmpleadoGenero().getNombre(), "Apellido",
                            polizaConsultar.getEmpleadoGenero().getApellido()),
                    "DetalleArticulo",
                    Map.of("SKU", polizaConsultar.getInventario().getSku(), "Nombre",
                            polizaConsultar.getInventario().getNombre())));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePoliza(@PathVariable Long id, @RequestBody Poliza polizaDetails) {
        return polizaRepository.findById(id).map(poliza -> {
            poliza.setCantidad(polizaDetails.getCantidad());
            polizaRepository.save(poliza);
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Mensaje",
                            Map.of("IDMensaje", "Se actualizó correctamente la poliza ## " + poliza.getIdPoliza()))));
        }).orElseGet(() -> ResponseEntity.status(500).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                Map.of("Mensaje", "Ha ocurrido un error al intentar actualizar la póliza."))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePoliza(@PathVariable Long id) {
        if (polizaRepository.existsById(id)) {
            polizaRepository.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Mensaje", Map.of("IDMensaje", "Se eliminó correctamente la poliza ## " + id))));
        } else {
            return ResponseEntity.status(500).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                    Map.of("Mensaje", "Ha ocurrido un error al intentar eliminar la póliza.")));
        }
    }
}

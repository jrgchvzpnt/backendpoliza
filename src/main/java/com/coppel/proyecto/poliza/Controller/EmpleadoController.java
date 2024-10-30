package com.coppel.proyecto.poliza.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.coppel.proyecto.poliza.Repository.EmpleadoRepository;
import com.coppel.proyecto.poliza.models.Empleado;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")

public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping
    public ResponseEntity<?> getAllEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        if (!empleados.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Empleados", empleados)));
        } else {
            return ResponseEntity.status(404).body(
                    Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                            Map.of("Mensaje", "No se encontraron empleados.")));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmpleado(@PathVariable Long id) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(id);
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Empleado", empleado)));
        } else {
            return ResponseEntity.status(404).body(
                    Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "Empleado no encontrado.")));
        }
    }

    @PostMapping
    public ResponseEntity<?> createEmpleado(@RequestBody Empleado empleado) {
        if (empleadoRepository.existsById(empleado.getIdEmpleado())) {
            return ResponseEntity.status(400).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                    Map.of("Mensaje", "ID de empleado ya existe.")));
        }
        empleadoRepository.save(empleado);
        return ResponseEntity.ok(Map.of(
                "Meta", Map.of("Status", "OK"),
                "Data", Map.of("Empleado", empleado)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpleado(@PathVariable Long id, @RequestBody Empleado empleadoDetails) {
        return empleadoRepository.findById(id).map(empleado -> {
            empleado.setNombre(empleadoDetails.getNombre());
            empleado.setApellido(empleadoDetails.getApellido());
            empleado.setPuesto(empleadoDetails.getPuesto());
            empleadoRepository.save(empleado);
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Empleado", empleado)));
        }).orElseGet(() -> ResponseEntity.status(404).body(
                Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "Empleado no encontrado."))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpleado(@PathVariable Long id) {
        if (empleadoRepository.existsById(id)) {
            empleadoRepository.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "Meta", Map.of("Status", "OK"),
                    "Data", Map.of("Mensaje", "Empleado eliminado correctamente.")));
        } else {
            return ResponseEntity.status(404).body(
                    Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "Empleado no encontrado.")));
        }
    }

}

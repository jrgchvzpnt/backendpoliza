package com.coppel.proyecto.poliza.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {

    private static final Logger logger = LoggerFactory.getLogger(PolizaController.class);

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
            return ResponseEntity.status(500).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                    Map.of("Mensaje", "Ha ocurrido un error al consultar la póliza.")));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPoliza(@RequestBody Poliza poliza) {

        logger.info("Iniciando creación de póliza con ID: {}", poliza.getIdPoliza());
        try {

            if (polizaRepository.existsById(poliza.getIdPoliza())) {
                logger.warn("El ID de la póliza ya existe: {}", poliza.getIdPoliza());
                return ResponseEntity.status(409).body(
                        Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                                Map.of("Mensaje", "ID de póliza ya existe.")));
            }

            if (!empleadoRepository.existsById(poliza.getEmpleadoGenero().getIdEmpleado())) {
                logger.warn("Empleado no existe: {}", poliza.getEmpleadoGenero().getIdEmpleado());
                return ResponseEntity.status(404).body(
                        Map.of("Meta", Map.of("Status", "FAILURE"), "Data", Map.of("Mensaje", "Empleado no existe.")));
            }

            Optional<Inventario> inventarioOpt = inventarioRepository.findById(poliza.getInventario().getSku());
            if (inventarioOpt.isEmpty()) {
                logger.warn("SKU no encontrado en inventario: {}", poliza.getInventario().getSku());
                return ResponseEntity.status(404).body(Map.of(
                        "Meta", Map.of("Status", "FAILURE"),
                        "Data", Map.of("Mensaje", "SKU no existe en el inventario.")));
            }

            Inventario inventario = inventarioOpt.get();
            if (inventario.getCantidad() < poliza.getCantidad()) {
                logger.warn("Cantidad insuficiente para SKU: {}. Disponible: {}, Solicitada: {}",
                        inventario.getSku(), inventario.getCantidad(), poliza.getCantidad());
                return ResponseEntity.status(400).body(Map.of(
                        "Meta", Map.of("Status", "FAILURE"),
                        "Data", Map.of(
                                "Mensaje", "No hay suficiente cantidad en inventario para el SKU solicitado.",
                                "Cantidad Restante", inventario.getCantidad())));
            }

            inventario.setCantidad(inventario.getCantidad() - poliza.getCantidad());
            inventarioRepository.save(inventario);
            logger.info("Cantidad en inventario actualizada para SKU: {}", inventario.getSku());

            // Guardar la nueva póliza
            polizaRepository.save(poliza);
            logger.info("Póliza guardada con éxito: {}", poliza.getIdPoliza());

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

        } catch (DataAccessException e) {
            logger.error("Ha ocurrido un error en los grabados de póliza con ID {}: ", poliza.getIdPoliza(),
                    e.getMessage());
            return ResponseEntity.status(500).body(
                    Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                            Map.of("Mensaje", "Ha ocurrido un error en los grabados de póliza")));
        } catch (Exception e) {
            logger.error("Ha ocurrido un error en los grabados de póliza con ID {}: ", poliza.getIdPoliza(),
                    e.getMessage());
            return ResponseEntity.status(400).body(
                    Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                            Map.of("Mensaje", "Ha ocurrido un error en los grabados de póliza")));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePoliza(@PathVariable Long id, @RequestBody Poliza polizaDetails) {
        try {
            return polizaRepository.findById(id).map(poliza -> {
                poliza.setCantidad(polizaDetails.getCantidad());
                polizaRepository.save(poliza);
                return ResponseEntity.ok(Map.of(
                        "Meta", Map.of("Status", "OK"),
                        "Data", Map.of("Mensaje",
                                Map.of("IDMensaje",
                                        "Se actualizó correctamente la poliza ## " + poliza.getIdPoliza()))));
            }).orElseGet(() -> ResponseEntity.status(500).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                    Map.of("Mensaje", "Ha ocurrido un error al intentar actualizar la póliza."))));
        } catch (Exception e) {
            logger.error("Ha ocurrido un error al intentar actualizar la póliza ", e.getMessage());
            return ResponseEntity.status(400).body(
                    Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                            Map.of("Mensaje", "Ha ocurrido un error al intentar actualizar la póliza")));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePoliza(@PathVariable Long id) {
        try {
            if (polizaRepository.existsById(id)) {
                polizaRepository.deleteById(id);
                return ResponseEntity.ok(Map.of(
                        "Meta", Map.of("Status", "OK"),
                        "Data", Map.of("Mensaje", Map.of("IDMensaje", "Se eliminó correctamente la poliza ## " + id))));
            } else {
                return ResponseEntity.status(404).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                        Map.of("Mensaje", "No Existe la póliza  a eliminar.")));
            }

        } catch (Exception e) {
            logger.error("Ha ocurrido un error al intentar actualizar la póliza ", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("Meta", Map.of("Status", "FAILURE"), "Data",
                    Map.of("Mensaje", "Ha ocurrido un error al intentar eliminar la póliza.")));
        }

    }
}

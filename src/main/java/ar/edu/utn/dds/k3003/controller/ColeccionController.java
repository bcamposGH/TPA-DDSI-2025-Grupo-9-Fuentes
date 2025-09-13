package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.config.Metricas;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.Timer;


import java.util.List;

@RestController
@RequestMapping("/api/colecciones")
public class ColeccionController {

    private final FachadaFuente fachadaFuente;
    private final Metricas metricas;

    @Autowired
    public ColeccionController(FachadaFuente fachadaFuente, Metricas metricas) {
        this.fachadaFuente = fachadaFuente;
        this.metricas = metricas;
    }

    @GetMapping
    public ResponseEntity<List<ColeccionDTO>> listarColecciones() {
        Timer.Sample timer = metricas.startTimer();
        try {
            List<ColeccionDTO> colecciones = fachadaFuente.colecciones();
            return ResponseEntity.ok(colecciones);
        } finally {
            metricas.stopTimer(timer, "colecciones.listar");
        }
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<ColeccionDTO> obtenerColeccion(@PathVariable String nombre) {
        return ResponseEntity.ok(fachadaFuente.buscarColeccionXId(nombre));
    }

    @PostMapping
    public ResponseEntity<ColeccionDTO> crearColeccion(@RequestBody ColeccionDTO coleccion) {
        Timer.Sample timer = metricas.startTimer();
        try {
            return ResponseEntity.ok(fachadaFuente.agregar(coleccion));
        } finally {
            metricas.stopTimer(timer, "colecciones.crear");
        }
    }
} 
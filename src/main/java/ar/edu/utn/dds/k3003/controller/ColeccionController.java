package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colecciones")
public class ColeccionController {

    private final FachadaFuente fachadaFuente;

    @Autowired
    public ColeccionController(FachadaFuente fachadaFuente) {
        this.fachadaFuente = fachadaFuente;
    }

    @GetMapping
    public ResponseEntity<List<ColeccionDTO>> listarColecciones() {
        return ResponseEntity.ok(fachadaFuente.colecciones());
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<ColeccionDTO> obtenerColeccion(@PathVariable String nombre) {
        return ResponseEntity.ok(fachadaFuente.buscarColeccionXId(nombre));
    }

    @PostMapping
    public ResponseEntity<ColeccionDTO> crearColeccion(@RequestBody ColeccionDTO coleccion) {
        return ResponseEntity.ok(fachadaFuente.agregar(coleccion));
    }
} 
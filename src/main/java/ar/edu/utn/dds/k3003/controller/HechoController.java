package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HechoController {

    private final FachadaFuente fachadaFuente;
    private final Fachada fachada;

    @Autowired
    public HechoController(FachadaFuente fachadaFuente, Fachada fachada) {
        this.fachadaFuente = fachadaFuente;
        this.fachada = fachada;
    }

    @GetMapping("/colecciones/{coleccionId}/hechos")
    public ResponseEntity<List<HechoDTO>> listarHechos(@PathVariable String coleccionId) {
        return ResponseEntity.ok(fachadaFuente.buscarHechosXColeccion(coleccionId));
    }

    @GetMapping("/hechos")
    public ResponseEntity<List<HechoDTO>> obtenerHechos() {
        List<HechoDTO> hechos = fachada.hechos();
        return ResponseEntity.ok(hechos);
    }

    @GetMapping("/hecho/{id}")
    public ResponseEntity<HechoDTO> obtenerHecho(@PathVariable String id) {
    HechoDTO hecho = fachadaFuente.buscarHechoXId(id);
        return ResponseEntity.ok(hecho);
}

    @PostMapping("/hecho")
    public ResponseEntity<HechoDTO> crearHecho(@RequestBody HechoDTO hecho) {
        return ResponseEntity.ok(fachadaFuente.agregar(hecho));
    }

    @PatchMapping("/hecho/{id}")
    public ResponseEntity<HechoDTO> actualizarHecho(@PathVariable String id, @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");

        if (nuevoEstado.equals("borrado")) {
            fachada.censurar(id);
        }
        HechoDTO hechoActualizado = fachadaFuente.buscarHechoXId(id);
        return ResponseEntity.ok(hechoActualizado);
    }

    @GetMapping("/hecho/{id}/pdis")
    public ResponseEntity<List<PdIDTO>> obtenerPdIsPorHecho(@PathVariable String id) {
        return ResponseEntity.ok(fachada.buscarPdIsPorHecho(id));
    }

    @PostMapping("/pdis")
    public ResponseEntity<PdIDTO> crearPdI(@RequestBody PdIDTO pdi) {
        PdIDTO procesada = fachada.agregar(pdi);
        return ResponseEntity.ok(procesada);
    }
}
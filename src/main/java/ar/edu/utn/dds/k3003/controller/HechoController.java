package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import io.micrometer.core.instrument.Timer;
import ar.edu.utn.dds.k3003.config.Metricas;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HechoController {

    private final FachadaFuente fachadaFuente;
    private final Fachada fachada;
    private final Metricas metricas;

    @Autowired
    public HechoController(FachadaFuente fachadaFuente, Fachada fachada, Metricas metricas) {
        this.fachadaFuente = fachadaFuente;
        this.fachada = fachada;
        this.metricas = metricas;
    }

    @GetMapping("/colecciones/{coleccionId}/hechos")
    public ResponseEntity<List<HechoDTO>> listarHechos(@PathVariable String coleccionId) {
        return ResponseEntity.ok(fachadaFuente.buscarHechosXColeccion(coleccionId));
    }
    
    @GetMapping("/colecciones/{coleccionId}/hechos-sin-solicitudes")
    public ResponseEntity<List<HechoDTO>> hechosSinSolicitudes(@PathVariable String coleccionId) {
        return ResponseEntity.ok(fachada.hechosSinSolicitudesPorColeccion(coleccionId));
    }
    
    @GetMapping("/hechos")
    public ResponseEntity<List<HechoDTO>> obtenerHechos() {
        Timer.Sample timer = metricas.startTimer();
         try {
             List<HechoDTO> hechos = fachada.hechos();
             return ResponseEntity.ok(hechos);
         } finally {
             metricas.stopTimer(timer, "hechos.listar");
         }
    }

    @GetMapping("/hecho/{id}")
    public ResponseEntity<HechoDTO> obtenerHecho(@PathVariable String id) {
    HechoDTO hecho = fachadaFuente.buscarHechoXId(id);
        return ResponseEntity.ok(hecho);
}

    @PostMapping("/hecho")
    public ResponseEntity<HechoDTO> crearHecho(@RequestBody HechoDTO hecho) {
        Timer.Sample timer = metricas.startTimer();
        try {
            return ResponseEntity.ok(fachadaFuente.agregar(hecho));
        } finally {
            metricas.stopTimer(timer, "hechos.crear");
        }
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

    @GetMapping("/pdis")
    public ResponseEntity<List<PdIDTO>> obtenerTodosLosPdIs() {
        return ResponseEntity.ok(fachada.obtenerTodosLosPdIs());
}

    @PostMapping("/pdis")
    public ResponseEntity<?> agregarPdI(@RequestBody PdIDTO pdi) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        System.out.println("[Fuente API] POST /api/pdis recibido con body:");
        try {
            System.out.println(mapper.writeValueAsString(pdi));
        } catch (Exception e) {
            System.out.println("No se pudo serializar el PdIDTO para log: " + e.getMessage());
        }

        try {
            System.out.println("[Fuente → Fachada] Llamando a Fachada.agregar(...)");
            PdIDTO procesada = fachada.agregar(pdi);

            System.out.println("[Fachada → Fuente API] Respuesta recibida del ProcesadorPdI:");
            try {
                System.out.println(mapper.writeValueAsString(procesada));
            } catch (Exception e) {
                System.out.println("No se pudo serializar la respuesta para log: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(procesada);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            System.out.println("[Fuente API] Error en ProcesadorPdI: " + e.getReason());
            e.printStackTrace();

            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(Map.of(
                            "status", e.getStatusCode().value(),
                            "error", ((HttpStatus) e.getStatusCode()).getReasonPhrase(),
                            "message", e.getReason()
                    ));

        } catch (Exception e) {
            System.out.println("[Fuente API] Error inesperado al procesar PdI: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", 500,
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

}
package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.repository.ColeccionRepository;
import ar.edu.utn.dds.k3003.repository.HechosRepository;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class ClearDB {

    private final ColeccionRepository coleccionRepository;
    private final HechosRepository hechosRepository;
    private final PdIRepository pdiRepository;

    @Autowired
    public ClearDB(
            ColeccionRepository coleccionRepository,
            HechosRepository hechosRepository,
            PdIRepository pdiRepository
    ) {
        this.coleccionRepository = coleccionRepository;
        this.hechosRepository = hechosRepository;
        this.pdiRepository = pdiRepository;
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> limpiarBase() {
        try {
            // Borrar en orden correcto (dependencias primero)
            pdiRepository.deleteAll();
            hechosRepository.deleteAll();
            coleccionRepository.deleteAll();

            return ResponseEntity.ok("Base de datos vaciada correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al vaciar la base: " + e.getMessage());
        }
    }
}
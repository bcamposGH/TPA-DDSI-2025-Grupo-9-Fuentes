package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.messaging.HechoSuscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messaging")
public class MessagingController {

    private final HechoSuscriber suscriber;

    @Autowired
    public MessagingController(HechoSuscriber suscriber) {
        this.suscriber = suscriber;
    }

    /**
     * Inicia la suscripción al topic/cola configurada.
     */
    @PostMapping("/start")
    public ResponseEntity<String> start() {
        try {
            suscriber.start();
            return ResponseEntity.ok("✅ Suscripción activada");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Error al activar: " + e.getMessage());
        }
    }

    /**
     * Detiene la suscripción activa, si existe.
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stop() {
        try {
            suscriber.stop();
            return ResponseEntity.ok("⏹️ Suscripción detenida");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Error al detener: " + e.getMessage());
        }
    }

    /**
     * Consulta el estado actual de la suscripción.
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Estado actual: " + (suscriber.isActivo() ? "Activo ✅" : "Inactivo ⏹️"));
    }
}
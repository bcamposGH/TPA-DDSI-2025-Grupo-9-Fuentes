package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.messaging.HechoSuscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messaging")
public class MessagingController {

    private final HechoSuscriber subscriber;

    @Autowired
    public MessagingController(HechoSuscriber subscriber) {
        this.subscriber = subscriber;
    }

    @PostMapping("/start")
    public ResponseEntity<String> start() {
        try {
            subscriber.start();
            return ResponseEntity.ok("✅ Suscripción activada");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Error al activar: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stop() {
        try {
            subscriber.stop();
            return ResponseEntity.ok("⏹️ Suscripción detenida");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Error al detener: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Estado: " + (subscriber.isActivo() ? "Activo" : "Inactivo"));
    }
}
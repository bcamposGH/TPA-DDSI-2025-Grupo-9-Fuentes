package ar.edu.utn.dds.k3003.config;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class HechoMetrics {

    private final MeterRegistry registry;

    public HechoMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void registrarHechoCreado() {
        registry.counter("hechos.creados").increment();
    }

    public void registrarPdiCreada() {
        registry.counter("pdis.creadas").increment();
    }
}
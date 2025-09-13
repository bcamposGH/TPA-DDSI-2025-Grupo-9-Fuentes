package ar.edu.utn.dds.k3003.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Metricas {

    private final MeterRegistry meterRegistry;

    public Metricas(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementCounter(String metricName, String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Tags must be provided as key-value pairs. Number of tags must be even.");
        }
        
        Counter.Builder counterBuilder = Counter.builder(metricName);
        for (int i = 0; i < tags.length; i += 2) {
            counterBuilder.tag(tags[i], tags[i + 1]);
        }
        counterBuilder.register(meterRegistry).increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String metricName, String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Tags must be provided as key-value pairs. Number of tags must be even.");
        }
        
        Timer.Builder timerBuilder = Timer.builder(metricName);
        for (int i = 0; i < tags.length; i += 2) {
            timerBuilder.tag(tags[i], tags[i + 1]);
        }
        sample.stop(timerBuilder.register(meterRegistry));
    }
    public void incrementCounterWithTags(String metricName, Map<String, String> tags) {
        Counter.Builder counterBuilder = Counter.builder(metricName);
        tags.forEach(counterBuilder::tag);
        counterBuilder.register(meterRegistry).increment();
    }

    public void stopTimerWithTags(Timer.Sample sample, String metricName, Map<String, String> tags) {
        Timer.Builder timerBuilder = Timer.builder(metricName);
        tags.forEach(timerBuilder::tag);
        sample.stop(timerBuilder.register(meterRegistry));
    }

    public void registrarHechoCreado() {
        incrementCounter("hechos.creados");
    }

    public void registrarHechoCensurado() {
        incrementCounter("hechos.censurados");
    }
}
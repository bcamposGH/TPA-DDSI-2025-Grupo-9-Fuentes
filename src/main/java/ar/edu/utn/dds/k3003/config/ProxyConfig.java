package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.clients.ProcesadorPdIProxy;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {

    @Bean
    public FachadaProcesadorPdI procesadorPdI(ObjectMapper objectMapper) {
        return new ProcesadorPdIProxy(objectMapper);
    }
}
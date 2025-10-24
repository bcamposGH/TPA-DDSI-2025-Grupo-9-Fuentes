package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProcesadorPdIProxy implements FachadaProcesadorPdI {

    private final String endpoint;
    private final ProcesadorPdIRetrofitClient service;
    private final ObjectMapper objectMapper;

    public ProcesadorPdIProxy(ObjectMapper objectMapper) {
        var env = System.getenv();
        this.endpoint = env.get("URL_PROCESADOR_PDI");

        // Configurar el ObjectMapper para fechas y nombres en snake_case
        objectMapper.registerModule(new JavaTimeModule()); // soporte para LocalDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // fechas en formato ISO-8601
        this.objectMapper = objectMapper;

        var retrofit = new Retrofit.Builder()
                .baseUrl(this.endpoint)
                .addConverterFactory(JacksonConverterFactory.create(this.objectMapper))
                .build();

        this.service = retrofit.create(ProcesadorPdIRetrofitClient.class);
    }

    @Override
    public PdIDTO procesar(PdIDTO pdiDTO) {
        try {
            // Log del JSON que vamos a enviar
            String json = objectMapper.writeValueAsString(pdiDTO);
            System.out.println("Fuente → ProcesadorPdI (request JSON): " + json);

            Response<PdIDTO> response = service.procesar(pdiDTO).execute();
            if (response.isSuccessful()) {
                System.out.println("ProcesadorPdI → Fuente (response OK): " + response.body());
                return response.body();
            } else {
                String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
                // Propagar mismo status y mensaje que devolvió ProcesadorPdI
                throw new ResponseStatusException(
                    HttpStatus.valueOf(response.code()),
                    "ProcesadorPdI → " + error
            );
        }
        } catch (Exception e) {
        // Error de conexión o excepción inesperada
            throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "No se pudo conectar con ProcesadorPdI",
                e
        );
    }
}

    @Override
    public PdIDTO buscarPdIPorId(String pdiId) {
        try {
            Response<PdIDTO> response = service.buscarPorId(pdiId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("PdI no encontrado o error en ProcesadorPdI: " + response.code() + " - " + error);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }

    @Override
    public java.util.List<PdIDTO> buscarPorHecho(String hechoId) {
        try {
            Response<java.util.List<PdIDTO>> response = service.buscarPorHecho(hechoId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("PdIs no encontrados o error en ProcesadorPdI: " + response.code() + " - " + error);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
    }

    @Override
    public List<PdIDTO> obtenerTodos() {
        try {
            Response<List<PdIDTO>> response = service.obtenerTodos().execute();

            if (response.isSuccessful()) {
                List<PdIDTO> body = response.body();
                if (body != null) {
                    return body;
                }
                throw new RuntimeException("ProcesadorPdI devolvió una respuesta vacía (sin body)");
            }

            String errorBody = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("Error en ProcesadorPdI (GET all): " + response.code() + " - " + errorBody);

        } catch (IOException e) {
            throw new RuntimeException("Error de red al conectar con ProcesadorPdI", e);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }
}

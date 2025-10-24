package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Response;
import org.springframework.stereotype.Component;

@Component
public class ProcesadorPdIProxy implements FachadaProcesadorPdI {

    private final String endpoint;
    private final ProcesadorPdIRetrofitClient service;
    private final ObjectMapper mapper;

    @SuppressWarnings("deprecation")
    public ProcesadorPdIProxy(ObjectMapper ignored) {
        var env = System.getenv();
        this.endpoint = env.get("URL_PROCESADOR_PDI");

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.configure(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        var retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(this.endpoint)
                .addConverterFactory(retrofit2.converter.jackson.JacksonConverterFactory.create(this.mapper))
                .build();

        this.service = retrofit.create(ProcesadorPdIRetrofitClient.class);
    }

    @Override
    public PdIDTO procesar(PdIDTO pdiDTO) {
        try {
            String json = mapper.writeValueAsString(pdiDTO);
            System.out.println("Fuente → ProcesadorPdI (request JSON): " + json);

            retrofit2.Response<PdIDTO> response = service.procesar(pdiDTO).execute();
            if (response.isSuccessful() && response.body() != null) {
                System.out.println("ProcesadorPdI → Fuente (response OK): " + response.body());
                return response.body();
            }
            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.valueOf(response.code()),
                    "ProcesadorPdI → " + error
            );
        } catch (Exception e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
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
    public java.util.List<PdIDTO> obtenerTodos() {
        try {
            retrofit2.Response<java.util.List<PdIDTO>> response = service.obtenerTodos().execute();
            System.out.println("Fuente → ProcesadorPdI (GET /pdis): " + response.code());
            if (response.isSuccessful() && response.body() != null) return response.body();

            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("Error en ProcesadorPdI (GET all): " + response.code() + " - " + error);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }
}

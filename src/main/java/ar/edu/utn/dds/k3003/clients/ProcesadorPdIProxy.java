package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.OkHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ProcesadorPdIProxy implements FachadaProcesadorPdI {

    private final String endpoint;
    private final ProcesadorPdIRetrofitClient service;
    private final ObjectMapper mapper;

    public ProcesadorPdIProxy(ObjectMapper ignored) {
        var env = System.getenv();
        this.endpoint = env.get("URL_PROCESADOR_PDI");

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.endpoint)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(this.mapper))
                .build();

        this.service = retrofit.create(ProcesadorPdIRetrofitClient.class);
    }

    @Override
    public PdIDTO procesar(PdIDTO pdiDTO) {
        try {
            String json = mapper.writeValueAsString(pdiDTO);
            System.out.println("Fuente → ProcesadorPdI (request JSON): " + json);

            retrofit2.Response<PdIDTO> response = service.procesar(pdiDTO).execute();

            // Caso 1: 200 OK con body vacío (nuevo comportamiento asincrónico)
            if (response.isSuccessful()) {

                boolean emptyBody =
                    response.body() == null ||
                    (response.errorBody() == null && response.raw().body().contentLength() == 0);

                if (emptyBody) {
                    System.out.println("ProcesadorPdI → OK (sin contenido). Procesamiento asincrónico aceptado.");
                    return pdiDTO; // devolvemos el mismo DTO enviado
                }

                // Caso 2: 200 OK con body (por compatibilidad)
                System.out.println("ProcesadorPdI → OK (con body).");
                return response.body();
            }

            // Caso 3: error HTTP
            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("ProcesadorPdI → " + error);

        } catch (Exception e) {
            // Si el error es "No content to map", interpretarlo como OK vacío
            if (e.getMessage() != null &&
                e.getMessage().contains("No content to map")) {

                System.out.println("ProcesadorPdI → OK (respuesta vacía detectada en catch).");
                return pdiDTO;
            }

            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con ProcesadorPdI: " + e.getMessage(),
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
            throw new ResponseStatusException(
                    HttpStatus.valueOf(response.code()),
                    "Error al buscar PdI: " + error
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con ProcesadorPdI",
                    e
            );
        }
    }

    @Override
    public List<PdIDTO> buscarPorHecho(String hechoId) {
        try {
            Response<List<PdIDTO>> response = service.buscarPorHecho(hechoId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new ResponseStatusException(
                    HttpStatus.valueOf(response.code()),
                    "Error al buscar PdIs por hecho: " + error
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con ProcesadorPdI",
                    e
            );
        }
    }

    @Override
    public List<PdIDTO> obtenerTodos() {
        try {
            Response<List<PdIDTO>> response = service.obtenerTodos().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new ResponseStatusException(
                    HttpStatus.valueOf(response.code()),
                    "Error al obtener PdIs: " + error
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con ProcesadorPdI",
                    e
            );
        }
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
        // No implementado
    }
}
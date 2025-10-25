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

    @SuppressWarnings("deprecation")
    public ProcesadorPdIProxy(ObjectMapper ignored) {
        var env = System.getenv();
        this.endpoint = env.get("URL_PROCESADOR_PDI");

        // 🔹 Configuración del ObjectMapper (sin snake_case, fechas ISO)
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        // 🔹 Configurar timeouts más amplios para evitar SocketTimeoutException
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // 🔹 Retrofit con Jackson y cliente HTTP configurado
        var retrofit = new Retrofit.Builder()
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

            Response<PdIDTO> response = service.procesar(pdiDTO).execute();

            if (response.isSuccessful() && response.body() != null) {
                System.out.println("ProcesadorPdI → Fuente (response OK): " + response.body());
                return response.body();
            }

            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new ResponseStatusException(
                    HttpStatus.valueOf(response.code()),
                    "ProcesadorPdI → " + error
            );

        } catch (SocketTimeoutException e) {
            throw new ResponseStatusException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "ProcesadorPdI no respondió a tiempo (timeout)",
                    e
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
    public PdIDTO buscarPdIPorId(String pdiId) {
        try {
            Response<PdIDTO> response = service.buscarPorId(pdiId).execute();
            System.out.println("Fuente → ProcesadorPdI (GET /pdis/" + pdiId + "): " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }

            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("PdI no encontrado o error en ProcesadorPdI: " + response.code() + " - " + error);

        } catch (SocketTimeoutException e) {
            throw new RuntimeException("Timeout al conectar con ProcesadorPdI", e);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }

    @Override
    public List<PdIDTO> buscarPorHecho(String hechoId) {
        try {
            Response<List<PdIDTO>> response = service.buscarPorHecho(hechoId).execute();
            System.out.println("Fuente → ProcesadorPdI (GET /pdis?hecho=" + hechoId + "): " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }

            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("PdIs no encontrados o error en ProcesadorPdI: " + response.code() + " - " + error);

        } catch (SocketTimeoutException e) {
            throw new RuntimeException("Timeout al conectar con ProcesadorPdI", e);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }

    @Override
    public List<PdIDTO> obtenerTodos() {
        try {
            Response<List<PdIDTO>> response = service.obtenerTodos().execute();
            System.out.println("Fuente → ProcesadorPdI (GET /pdis): " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }

            String error = response.errorBody() != null ? response.errorBody().string() : "sin detalle";
            throw new RuntimeException("Error en ProcesadorPdI (GET all): " + response.code() + " - " + error);

        } catch (SocketTimeoutException e) {
            throw new RuntimeException("Timeout al conectar con ProcesadorPdI", e);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
    }
}
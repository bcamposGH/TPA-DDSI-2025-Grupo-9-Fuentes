package ar.edu.utn.dds.k3003.clients;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class ProcesadorPdIProxy {

    private final String endpoint;
    private final ProcesadorPdIRetrofitClient service;

    public ProcesadorPdIProxy(ObjectMapper objectMapper) {
        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_PDIS", "http://localhost:8082/");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = ((Retrofit) retrofit).create(ProcesadorPdIRetrofitClient.class);
    }

    @SneakyThrows
    public PdIDTO crear(PdIDTO dto) {
        Response<PdIDTO> execute = service.procesarPdI(dto).execute();
        if (execute.isSuccessful()) {
            return execute.body();
        }
        throw new RuntimeException("Error creando PdI en ProcesadorPdI");
    }

    @SneakyThrows
    public PdIDTO buscarPorId(String id) throws NoSuchElementException {
        Response<PdIDTO> execute = service.buscarPorId(id).execute();

        if (execute.isSuccessful()) {
            return execute.body();
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("No se encontró el PdI con id " + id);
        }
        throw new RuntimeException("Error conectando con ProcesadorPdI");
    }

    @SneakyThrows
    public List<PdIDTO> buscarPorHecho(String hechoId) {
        Response<List<PdIDTO>> execute = service.buscarPorHecho(hechoId).execute();
        if (execute.isSuccessful()) {
            return execute.body();
        }
        throw new RuntimeException("Error consultando PdIs por hecho " + hechoId);
    }
}
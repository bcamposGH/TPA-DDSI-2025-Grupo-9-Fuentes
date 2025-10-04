package ar.edu.utn.dds.k3003.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.Map;

public class SolicitudesProxy {

    private final SolicitudesRetrofitClient service;

    public SolicitudesProxy(ObjectMapper objectMapper) {
        var env = System.getenv();
        String endpoint = env.get("URL_SOLICITUDES");

        var retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        this.service = retrofit.create(SolicitudesRetrofitClient.class);
    }

    public List<String> hechosSinSolicitudes(List<String> ids) {
        try {
            Response<List<String>> response = service.hechosSinSolicitudes(Map.of("ids", ids)).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            throw new RuntimeException("Error en Solicitudes: " + response.code());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con Solicitudes", e);
        }
    }
}
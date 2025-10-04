package ar.edu.utn.dds.k3003.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.Map;

/**
 * Proxy para comunicarse con el microservicio de Solicitudes.
 * Llama a /solicitudes/hechos/hechos-sin-solicitudes enviando una lista de IDs
 * y devuelve la lista de IDs de hechos que no tienen solicitudes.
 */
public class SolicitudesProxy {

    private final SolicitudesRetrofitClient service;
    private final ObjectMapper mapper;

    public SolicitudesProxy(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
        var env = System.getenv();
        String endpoint = env.get("URL_SOLICITUDES");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException("⚠️ Variable de entorno URL_SOLICITUDES no configurada");
        }

        System.out.println("🔗 [SolicitudesProxy] Endpoint configurado: " + endpoint);

        var retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        this.service = retrofit.create(SolicitudesRetrofitClient.class);
    }

    /**
     * Devuelve la lista de IDs de hechos que no tienen solicitudes activas.
     * @param ids lista de IDs de hechos a consultar
     * @return lista de IDs sin solicitudes
     */
    public List<String> hechosSinSolicitudes(List<String> ids) {
        try {
            // Log del request
            String jsonRequest = mapper.writeValueAsString(Map.of("ids", ids));
            System.out.println("📤 [Fuente → Solicitudes] Request JSON: " + jsonRequest);

            System.out.println("🌐 [SolicitudesProxy] URL llamada: "
                + service.hechosSinSolicitudes(Map.of("ids", ids)).request().url());
            
            // Llamada HTTP
            Response<HechosSinSolicitudesResponse> response =
                    service.hechosSinSolicitudes(Map.of("ids", ids)).execute();

            // Log del response HTTP
            System.out.println("📥 [Solicitudes → Fuente] Response code: " + response.code());

            if (response.errorBody() != null) {
                System.out.println("❗ [Solicitudes → Fuente] Error body: " + response.errorBody().string());
            }

            if (response.isSuccessful() && response.body() != null) {
                List<String> resultado = response.body().hechossinSolicitudes();
                System.out.println("✅ [Solicitudes → Fuente] Hechos sin solicitudes: " + resultado);
                return resultado;
            }

            throw new RuntimeException("Error en Solicitudes: " + response.code());
        } catch (Exception e) {
            System.err.println("💥 [SolicitudesProxy] Error conectando con Solicitudes: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo conectar con Solicitudes", e);
        }
    }
}
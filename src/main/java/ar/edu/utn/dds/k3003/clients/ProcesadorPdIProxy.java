package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ProcesadorPdIProxy implements FachadaProcesadorPdI {

    private final String endpoint;
    private final ProcesadorPdIRetrofitClient service;

    public ProcesadorPdIProxy(ObjectMapper objectMapper) {
        var env = System.getenv();
        this.endpoint = env.get("URL_PROCESADOR_PDI");

        var retrofit = new Retrofit.Builder()
                .baseUrl(this.endpoint)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        this.service = retrofit.create(ProcesadorPdIRetrofitClient.class);
    }

    @Override
    public PdIDTO procesar(PdIDTO pdiDTO) {
        try {
            Response<PdIDTO> response = service.procesar(pdiDTO).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            throw new RuntimeException("Error en ProcesadorPdI: " + response.code());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }

    @Override
    public PdIDTO buscarPdIPorId(String pdiId) {
        try {
            Response<PdIDTO> response = service.buscarPorId(pdiId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            throw new RuntimeException("PdI no encontrado o error en ProcesadorPdI: " + response.code());
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
            throw new RuntimeException("PdIs no encontrados o error en ProcesadorPdI: " + response.code());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con ProcesadorPdI", e);
        }
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
        return;
    }
}
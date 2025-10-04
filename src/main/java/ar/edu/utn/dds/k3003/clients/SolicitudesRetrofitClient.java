package ar.edu.utn.dds.k3003.clients;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.List;
import java.util.Map;

public interface SolicitudesRetrofitClient {

    @POST("solicitudes/hechos/hechos-sin-solicitudes")
    Call<HechosSinSolicitudesResponse> hechosSinSolicitudes(@Body Map<String, List<String>> ids);
}
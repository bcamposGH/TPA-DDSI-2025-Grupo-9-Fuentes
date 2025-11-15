package ar.edu.utn.dds.k3003.clients;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

public interface ProcesadorPdIRetrofitClient {
    @GET("pdis")
    Call<List<PdIDTO>> obtenerTodos();

    @GET("pdis/{id}")
    Call<PdIDTO> buscarPorId(@Path("id") String id);

    @GET("pdis")
    Call<List<PdIDTO>> buscarPorHecho(@Query("hecho") String hechoId);

    @POST("/pdis")
    Call<Void> procesarAsync(@Body PdIDTO pdiDTO);
}
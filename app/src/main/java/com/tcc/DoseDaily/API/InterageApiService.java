package com.tcc.DoseDaily.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface InterageApiService {

    @GET("/v1/medicamentos/")
    Call<ApiResponse<List<Medicamento>>> getMedicamentos(@Header("Authorization") String token);

    @GET("/v1/principios-ativos/{id}/interacoes/")
    Call<ApiResponse<List<Interacao>>> getInteracoesPorPrincipioAtivo(
            @Header("Authorization") String token,
            @Path("id") int primeiroPrincipioAtivoId,
            @Query("principios_ativos") int segundoPrincipioAtivoId
    );

    @GET("/v1/principios-ativos/{id}/interacoes/")
    Call<ApiResponse<List<Interacao>>> getInteracoesPorPrincipioAtivo(
            @Header("Authorization") String token,
            @Path("id") int principioAtivoId
    );

    @GET("/v1/principios-ativos/")
    Call<ApiResponse<List<Interacao.PrincipioAtivo>>> getPrincipiosAtivos(@Header("Authorization") String token);

}

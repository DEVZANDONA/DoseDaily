package com.tcc.DoseDaily.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

import java.util.List;

public interface InterageApiService {
    @GET("/v1/medicamentos/")
    Call<ApiResponse<List<Medicamento>>> getMedicamentos(@Header("Authorization") String token);
}
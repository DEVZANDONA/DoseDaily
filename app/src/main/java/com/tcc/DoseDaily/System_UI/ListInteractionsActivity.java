package com.tcc.DoseDaily.System_UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tcc.DoseDaily.API.ApiClient;
import com.tcc.DoseDaily.API.ApiResponse;
import com.tcc.DoseDaily.API.InterageApiService;
import com.tcc.DoseDaily.API.Medicamento;
import com.tcc.DoseDaily.Adapters.MedicamentoAdapter;
import com.tcc.DoseDaily.Adapters.MedicamentoAdapter.OnItemClickListener;
import com.tcc.DoseDaily.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListInteractionsActivity extends AppCompatActivity {

    private InterageApiService apiService;
    private RecyclerView recyclerView;
    private MedicamentoAdapter medicamentoAdapter;
    private ImageView fab;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos_api);

        apiService = ApiClient.getClient().create(InterageApiService.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.side_ic);

        drawerLayout = findViewById(R.id.drawer_layout2);

        fab.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        String suaChave = "5d36f30b7ecc4bd2c8fee862958c75b99a837499";
        Call<ApiResponse<List<Medicamento>>> medicamentosCall = apiService.getMedicamentos("Token " + suaChave);
        medicamentosCall.enqueue(new Callback<ApiResponse<List<Medicamento>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Medicamento>>> call, Response<ApiResponse<List<Medicamento>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Medicamento>> apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getResults() != null && !apiResponse.getResults().isEmpty()) {
                        List<Medicamento> medicamentos = apiResponse.getResults();

                        // Adicione logs para entender o que está sendo retornado
                        Log.d("API_RESPONSE", "Medicamentos: " + medicamentos.toString());

                        // Atualizar a RecyclerView com a lista de medicamentos
                        medicamentoAdapter = new MedicamentoAdapter(medicamentos, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Medicamento medicamento) {
                                // Ação a ser realizada ao clicar em um medicamento
                                openInteractionsActivity(medicamento);
                            }
                        });
                        recyclerView.setAdapter(medicamentoAdapter);
                    } else {
                        // Não há resultados ou a lista de medicamentos está vazia
                        Log.w("API_RESPONSE", "A resposta da API não contém medicamentos.");
                    }
                } else {
                    // Handle erros aqui
                    Log.e("API_RESPONSE", "Erro na resposta da API: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Medicamento>>> call, Throwable t) {
                // Trate falhas na comunicação aqui
                Log.e("API_RESPONSE", "Falha na comunicação com a API: " + t.getMessage());
            }
        });
    }

    private void openInteractionsActivity(Medicamento medicamento) {
        Intent intent = new Intent(this, InteractionsActivity.class);
        intent.putExtra("MEDICAMENTO_ID", medicamento.getId());
        startActivity(intent);
    }
}

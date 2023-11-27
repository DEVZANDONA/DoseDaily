package com.tcc.DoseDaily.System_UI;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tcc.DoseDaily.API.ApiClient;
import com.tcc.DoseDaily.API.ApiResponse;
import com.tcc.DoseDaily.API.Interacao;
import com.tcc.DoseDaily.API.InterageApiService;
import com.tcc.DoseDaily.Adapters.InteracaoAdapter;
import com.tcc.DoseDaily.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractionsActivity extends AppCompatActivity {

    private InterageApiService apiService;
    private RecyclerView recyclerView;
    private InteracaoAdapter interacaoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactions);

        // Inicialize o serviço da API
        apiService = ApiClient.getClient().create(InterageApiService.class);

        // Inicialize a RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtenha o ID do medicamento selecionado (você pode passar isso através do Intent)
        int medicamentoId = getIntent().getIntExtra("MEDICAMENTO_ID", -1);

        if (medicamentoId != -1) {
            // Configurar a chamada à API para obter a lista de interações para o medicamento específico
            Call<ApiResponse<List<Interacao>>> interacoesCall = apiService.getInteracoesDoMedicamento(medicamentoId);
            interacoesCall.enqueue(new Callback<ApiResponse<List<Interacao>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Interacao>>> call, Response<ApiResponse<List<Interacao>>> response) {
                    if (response.isSuccessful()) {
                        ApiResponse<List<Interacao>> apiResponse = response.body();

                        if (apiResponse != null && apiResponse.getResults() != null && !apiResponse.getResults().isEmpty()) {
                            List<Interacao> interacoes = apiResponse.getResults();

                            // Adicione logs para entender o que está sendo retornado
                            Log.d("API_RESPONSE", "Interações: " + interacoes.toString());

                            // Atualizar a RecyclerView com a lista de interações
                            interacaoAdapter = new InteracaoAdapter(interacoes);
                            recyclerView.setAdapter(interacaoAdapter);
                        } else {
                            // Não há resultados ou a lista de interações está vazia
                            Log.w("API_RESPONSE", "A resposta da API não contém interações.");
                        }
                    } else {
                        // Handle erros aqui
                        Log.e("API_RESPONSE", "Erro na resposta da API: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Interacao>>> call, Throwable t) {
                    // Trate falhas na comunicação aqui
                    Log.e("API_RESPONSE", "Falha na comunicação com a API: " + t.getMessage());
                }
            });
        } else {
            // ID do medicamento não foi passado corretamente
            Log.e("INTERACTIONS_ACTIVITY", "ID do medicamento não foi passado corretamente.");
        }
    }
}

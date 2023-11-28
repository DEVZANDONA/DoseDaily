package com.tcc.DoseDaily.System_UI;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.API.ApiClient;
import com.tcc.DoseDaily.API.ApiResponse;
import com.tcc.DoseDaily.API.Interacao;
import com.tcc.DoseDaily.API.InterageApiService;
import com.tcc.DoseDaily.Adapters.PrincipioAtivoAdapter;
import com.tcc.DoseDaily.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListInteractionsActivity extends AppCompatActivity {

    private InterageApiService apiService;
    private RecyclerView recyclerView;
    private PrincipioAtivoAdapter principioAtivoAdapter;
    private ImageView fab;
    private DrawerLayout drawerLayout;
    private List<Interacao.PrincipioAtivo> principiosAtivosSelecionados = new ArrayList<>();
    private Integer primeiroPrincipioAtivoId;
    private Integer segundoPrincipioAtivoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos_api);

        initializeViews();

        apiService = ApiClient.getClient().create(InterageApiService.class);

        fab.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        String suaChave = "5d36f30b7ecc4bd2c8fee862958c75b99a837499";
        loadPrincipiosAtivosFromApi(suaChave);
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = findViewById(R.id.side_ic);
        drawerLayout = findViewById(R.id.drawer_layout2);
    }

    private void loadPrincipiosAtivosFromApi(String token) {
        Call<ApiResponse<List<Interacao.PrincipioAtivo>>> principiosAtivosCall = apiService.getPrincipiosAtivos("Token " + token);
        principiosAtivosCall.enqueue(new Callback<ApiResponse<List<Interacao.PrincipioAtivo>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Interacao.PrincipioAtivo>>> call, Response<ApiResponse<List<Interacao.PrincipioAtivo>>> response) {
                handlePrincipiosAtivosApiResponse(response);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Interacao.PrincipioAtivo>>> call, Throwable t) {
                Log.e("API_RESPONSE", "Falha na comunicação com a API: " + t.getMessage());
            }
        });
    }

    private void handlePrincipiosAtivosApiResponse(Response<ApiResponse<List<Interacao.PrincipioAtivo>>> response) {
        if (response.isSuccessful()) {
            ApiResponse<List<Interacao.PrincipioAtivo>> apiResponse = response.body();
            if (apiResponse != null && apiResponse.getResults() != null && !apiResponse.getResults().isEmpty()) {
                List<Interacao.PrincipioAtivo> principiosAtivos = apiResponse.getResults();
                handlePrincipiosAtivosList(principiosAtivos);
            } else {
                Log.w("API_RESPONSE", "A resposta da API não contém princípios ativos.");
            }
        } else {
            Log.e("API_RESPONSE", "Erro na resposta da API: " + response.message());
        }
    }

    private void handlePrincipiosAtivosList(List<Interacao.PrincipioAtivo> principiosAtivos) {
        Log.d("API_RESPONSE", "Princípios Ativos: " + principiosAtivos.toString());
        principioAtivoAdapter = new PrincipioAtivoAdapter(principiosAtivos, new PrincipioAtivoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Interacao.PrincipioAtivo principioAtivo) {
                selecionarPrincipioAtivo(principioAtivo);
            }
        });
        recyclerView.setAdapter(principioAtivoAdapter);
    }

    private void selecionarPrincipioAtivo(Interacao.PrincipioAtivo principioAtivo) {
        Log.d("PrincipioAtivoSelecionado", "ID: " + principioAtivo.getId() + ", Nome: " + principioAtivo.getNome());

        if (principiosAtivosSelecionados.size() < 2) {
            addPrincipioAtivoToList(principioAtivo);
        } else {
            Toast.makeText(this, "Apenas dois princípios ativos podem ser selecionados de cada vez.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPrincipioAtivoToList(Interacao.PrincipioAtivo principioAtivo) {
        if (!principiosAtivosSelecionados.contains(principioAtivo)) {
            principiosAtivosSelecionados.add(principioAtivo);

            // Armazenar IDs dos princípios ativos
            if (principiosAtivosSelecionados.size() == 1) {
                primeiroPrincipioAtivoId = principioAtivo.getId();
            } else if (principiosAtivosSelecionados.size() == 2) {
                segundoPrincipioAtivoId = principioAtivo.getId();
                // Buscar interações após selecionar os dois princípios ativos
                buscarInteracoes(primeiroPrincipioAtivoId, segundoPrincipioAtivoId);
            }
        } else {
            Toast.makeText(this, "Este princípio ativo já foi selecionado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarInteracoes(Integer primeiroPrincipioAtivoId, Integer segundoPrincipioAtivoId) {
        // Chamada à API para obter interações entre os dois princípios ativos
        String suaChave = "5d36f30b7ecc4bd2c8fee862958c75b99a837499";
        String token = "Token " + suaChave;

        Call<ApiResponse<List<Interacao>>> interacoesCall = apiService.getInteracoesPorPrincipioAtivo(token, primeiroPrincipioAtivoId, segundoPrincipioAtivoId);

        interacoesCall.enqueue(new Callback<ApiResponse<List<Interacao>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Interacao>>> call, Response<ApiResponse<List<Interacao>>> response) {
                handleInteracoesApiResponse(response);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Interacao>>> call, Throwable t) {
                Log.e("API_RESPONSE", "Falha na comunicação com a API: " + t.getMessage());
            }
        });
    }

    private void handleInteracoesApiResponse(Response<ApiResponse<List<Interacao>>> response) {
        if (response.isSuccessful()) {
            ApiResponse<List<Interacao>> apiResponse = response.body();

            if (apiResponse != null && apiResponse.getResults() != null && !apiResponse.getResults().isEmpty()) {
                // Filtrar interações para encontrar aquelas com o segundo princípio ativo
                List<Interacao> todasInteracoes = apiResponse.getResults();
                List<Interacao> interacoesFiltradas = filtrarInteracoes(todasInteracoes, segundoPrincipioAtivoId);

                // Exibir diálogo com informações das interações encontradas
                if (!interacoesFiltradas.isEmpty()) {
                    Interacao primeiraInteracao = interacoesFiltradas.get(0); // Assumindo que você está interessado na primeira interação da lista
                    exibirDialogoComExplicacao(primeiraInteracao.getExplicacao(), primeiraInteracao.getGravidade());
                } else {
                    // Exibir diálogo informando que não há interações registradas
                    exibirDialogoSemInteracoes();
                }
            } else {
                // Exibir diálogo informando que não há interações registradas
                exibirDialogoSemInteracoes();
            }
        } else {
            Log.e("API_RESPONSE", "Erro na resposta da API: " + response.message());
        }
    }

    private List<Interacao> filtrarInteracoes(List<Interacao> todasInteracoes, int segundoPrincipioAtivoId) {
        List<Interacao> interacoesFiltradas = new ArrayList<>();

        for (Interacao interacao : todasInteracoes) {
            List<Interacao.PrincipioAtivo> principiosAtivos = interacao.getPrincipiosAtivos();
            if (principiosAtivos != null && principiosAtivos.size() == 2) {
                int idDoPrimeiroPrincipioAtivo = principiosAtivos.get(0).getId();
                int idDoSegundoPrincipioAtivo = principiosAtivos.get(1).getId();

                if ((idDoPrimeiroPrincipioAtivo == primeiroPrincipioAtivoId && idDoSegundoPrincipioAtivo == segundoPrincipioAtivoId) ||
                        (idDoPrimeiroPrincipioAtivo == segundoPrincipioAtivoId && idDoSegundoPrincipioAtivo == primeiroPrincipioAtivoId)) {
                    interacoesFiltradas.add(interacao);
                }
            }
        }

        return interacoesFiltradas;
    }

    private void exibirDialogoComExplicacao(String explicacao, String gravidade) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Explicação: " + explicacao + "\nGravidade: " + gravidade)
                .setTitle("Explicação da Interação")
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    principiosAtivosSelecionados.clear();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exibirDialogoSemInteracoes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Não há interações registradas entre os princípios ativos selecionados.")
                .setTitle("Sem Interações")
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    principiosAtivosSelecionados.clear();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
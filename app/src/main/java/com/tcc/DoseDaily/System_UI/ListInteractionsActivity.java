package com.tcc.DoseDaily.System_UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tcc.DoseDaily.API.ApiClient;
import com.tcc.DoseDaily.API.ApiResponse;
import com.tcc.DoseDaily.API.Interacao;
import com.tcc.DoseDaily.API.InterageApiService;
import com.tcc.DoseDaily.Adapters.PrincipioAtivoAdapter;
import com.tcc.DoseDaily.Auth.LoginActivity;
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
    private boolean snackbarExibida = false;

    private NavigationView navigationView;

    private String userId;

    private SearchView searchView;

    private SideBar sideBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos_api);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        initializeViews();

        drawerLayout = findViewById(R.id.drawer_layout9);
        navigationView = findViewById(R.id.nav_view9);

        // Inicialize a SideBar
        sideBar = new SideBar();
        sideBar.setupDrawer(this, drawerLayout, navigationView, userId);

        // Configuração do clique no ícone lateral (side_ic)
        ImageView sideIcon = findViewById(R.id.side_ic);
        sideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abra a SideBar quando o ícone for clicado
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        apiService = ApiClient.getClient().create(InterageApiService.class);

        fab.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        String suaChave = "5d36f30b7ecc4bd2c8fee862958c75b99a837499";
        loadPrincipiosAtivosFromApi(suaChave);
    }

    private void initializeViews() {
        SearchView searchView = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = findViewById(R.id.side_ic);
        drawerLayout = findViewById(R.id.drawer_layout2);

        // Configuração do SearchView

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Pesquisar...");
        searchView.setFocusable(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (principioAtivoAdapter != null) {
                    principioAtivoAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
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
                if (!snackbarExibida) {
                    // Exibe a Snackbar apenas no primeiro clique
                    mostrarSnackbar("Pressione outro medicamento para visualizar a interação");
                    snackbarExibida = true;  // Define a flag para indicar que a Snackbar foi exibida
                }

                Log.d("PrincipioAtivoSelecionado", "ID: " + principioAtivo.getId() + ", Nome: " + principioAtivo.getNome());

                if (principiosAtivosSelecionados.size() < 2) {
                    addPrincipioAtivoToList(principioAtivo);
                } else {
                    Toast.makeText(ListInteractionsActivity.this, "Apenas dois princípios ativos podem ser selecionados de cada vez.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(principioAtivoAdapter);
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
            mostrarSnackbar("Este princípio ativo já foi selecionado.");
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

        // Configurar o layout do AlertDialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER); // Centralizar o layout

        // Configurar o texto do título
        TextView tituloTextView = new TextView(this);
        tituloTextView.setText("Explicação da Interação");
        tituloTextView.setTypeface(null, Typeface.BOLD);
        tituloTextView.setTextSize(18); // Tamanho do texto em sp
        tituloTextView.setGravity(Gravity.CENTER); // Centralizar o título
        layout.addView(tituloTextView);

        // Configurar o texto da explicação
        TextView explicacaoTextView = new TextView(this);
        explicacaoTextView.setText("Explicação: " + explicacao);
        explicacaoTextView.setTypeface(null, Typeface.BOLD);
        explicacaoTextView.setTextSize(16); // Tamanho do texto em sp
        explicacaoTextView.setGravity(Gravity.START); // Alinhar à esquerda (pode ajustar conforme necessário)
        layout.addView(explicacaoTextView);

        // Configurar o texto da gravidade
        TextView gravidadeTextView = new TextView(this);
        gravidadeTextView.setText("Gravidade: " + gravidade); // Adicionado o valor da gravidade na mesma linha
        gravidadeTextView.setTypeface(null, Typeface.BOLD);
        gravidadeTextView.setTextSize(16); // Tamanho do texto em sp
        gravidadeTextView.setTextColor(Color.RED);
        gravidadeTextView.setGravity(Gravity.START); // Alinhar à esquerda
        layout.addView(gravidadeTextView);

        // Aplicar o layout personalizado ao AlertDialog
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, id) -> {
            dialog.dismiss();
            principiosAtivosSelecionados.clear();
            snackbarExibida = false;
            principioAtivoAdapter.clearSelection(); // Limpar a seleção
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        dialog.show();
    }

    private void exibirDialogoSemInteracoes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Não há interações registradas entre os princípios ativos selecionados.")
                .setTitle("Sem Interações")
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    principiosAtivosSelecionados.clear();
                    snackbarExibida = false;
                    principioAtivoAdapter.clearSelection();
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        dialog.show();
    }

    private void mostrarSnackbar(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        // Aplicar a forma personalizada
        snackbarView.setBackground(ContextCompat.getDrawable(this, R.drawable.snackbar_bg));
        // Centralizar a mensagem
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // Fonte em negrito
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        snackbar.show();
    }
}

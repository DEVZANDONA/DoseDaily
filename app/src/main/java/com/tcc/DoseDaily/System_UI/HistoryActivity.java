package com.tcc.DoseDaily.System_UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tcc.DoseDaily.Adapters.DayDividerItemDecoration;
import com.tcc.DoseDaily.Adapters.HistoricAdapter;
import com.tcc.DoseDaily.Models.HistoryItem;
import com.tcc.DoseDaily.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoricAdapter historicAdapter;
    private List<HistoricAdapter.Item> itemList;
    private DatabaseReference historicoReference;

    private DrawerLayout drawerLayout;
    private SideBar sideBar;

    String userId;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        drawerLayout = findViewById(R.id.drawer_layout5);
        navigationView = findViewById(R.id.nav_view5);

        // Verifica se o usuário está autenticado
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

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

        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        Button btnLimparHistorico = findViewById(R.id.btnLimparHistorico);

        // Configurar o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adicionar o ItemDecoration para adicionar divisores entre as seções
        recyclerView.addItemDecoration(new DayDividerItemDecoration(this, getResources().getDimensionPixelSize(R.dimen.divider_height)));

        historicAdapter = new HistoricAdapter(itemList);
        recyclerView.setAdapter(historicAdapter);

        // Obter uma referência ao nó "Histórico" no RealTimeDatabase
        historicoReference = FirebaseDatabase.getInstance().getReference("Histórico");

        // Carregar os dados do histórico
        loadHistoryData();

        // Adicionar um listener ao botão para limpar o histórico (se necessário)
        btnLimparHistorico.setOnClickListener(view -> clearHistory());
    }

    private void loadHistoryData() {
        historicoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // Limpar a lista para evitar duplicatas

                // Adicionar todas as divisões com os dias da semana
                String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

                for (String dayOfWeek : daysOfWeek) {
                    itemList.add(new HistoricAdapter.SectionItem(dayOfWeek));

                    // Adicionar os itens correspondentes a cada divisão
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HistoryItem historyItem = snapshot.getValue(HistoryItem.class);

                        if (historyItem != null && historyItem.getDiaDaSemana().equals(dayOfWeek)) {
                            // Criar um objeto ContentItem
                            HistoricAdapter.ContentItem contentItem = new HistoricAdapter.ContentItem(historyItem);

                            // Adicionar o item à lista
                            itemList.add(contentItem);
                        }
                    }
                }

                historicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Lidar com erros, se necessário
            }
        });
    }


    private void clearHistory() {
        // Remover todos os registros do nó "Histórico"
        historicoReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        historicAdapter.notifyDataSetChanged();
                    }
                });
    }
}

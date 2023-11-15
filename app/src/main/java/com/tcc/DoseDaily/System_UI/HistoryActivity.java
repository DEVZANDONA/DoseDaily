package com.tcc.DoseDaily.System_UI;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        Button btnLimparHistorico = findViewById(R.id.btnLimparHistorico);

        // Configurar o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        // Adicionar um listener para escutar as alterações no nó "Histórico"
        historicoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // Limpar a lista para evitar duplicatas

                // Iterar sobre os registros do histórico
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HistoryItem historyItem = snapshot.getValue(HistoryItem.class);

                    if (historyItem != null) {
                        // Criar um objeto ContentItem
                        HistoricAdapter.ContentItem contentItem = new HistoricAdapter.ContentItem(historyItem);

                        // Adicionar os itens à lista
                        itemList.add(contentItem);
                    }
                }

                // Notificar o adapter sobre as alterações nos dados
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
                        itemList.clear(); // Limpar a lista localmente
                        historicAdapter.notifyDataSetChanged();
                    }
                });
    }
}

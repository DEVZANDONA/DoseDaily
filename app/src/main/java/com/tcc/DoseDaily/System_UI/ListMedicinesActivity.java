package com.tcc.DoseDaily.System_UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tcc.DoseDaily.Adapters.MyAdapter;
import com.tcc.DoseDaily.Auth.LoginActivity;
import com.tcc.DoseDaily.Models.Medicines;
import com.tcc.DoseDaily.System_UI.Database.UploadMedicineActivity;
import com.tcc.DoseDaily.R;
import java.util.ArrayList;
import java.util.List;

public class ListMedicinesActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private Query userMedicamentoQuery;
    private ValueEventListener eventListener;
    private RecyclerView recyclerView;
    private List<Medicines> dataList;
    private MyAdapter adapter;
    private SearchView searchView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);

        // Verifica se o usuário está autenticado
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // Se o usuário não estiver autenticado, redirecione-o para a tela de login
            Intent intent = new Intent(ListMedicinesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Encerre esta atividade para que o usuário não possa voltar pressionando o botão "voltar"
            return; // Saia do método para evitar a execução do código restante
        }

        // Obtenha o ID do usuário atual usando Firebase Authentication
        userId = user.getUid();

        // Construa a Query de medicamentos do usuário usando o ID do usuário
        userMedicamentoQuery = FirebaseDatabase.getInstance().getReference("Medicamento")
                .orderByChild("userId").equalTo(userId);

        // Configuração da interface do usuário
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ListMedicinesActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        dataList = new ArrayList<>();
        adapter = new MyAdapter(ListMedicinesActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListMedicinesActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Recupere dados do banco de dados e configure o adaptador
        dialog.show();
        eventListener = userMedicamentoQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ListaMedicamentos", "Número de medicamentos encontrados: " + snapshot.getChildrenCount());
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Medicines medicines = itemSnapshot.getValue(Medicines.class);
                    medicines.setKey(itemSnapshot.getKey());
                    dataList.add(medicines);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ListaMedicamentos", "Erro ao recuperar medicamentos: " + error.getMessage());
                dialog.dismiss();
            }
        });

        // Configuração da funcionalidade de pesquisa
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        // Configuração do botão de adicionar novo medicamento
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListMedicinesActivity.this, UploadMedicineActivity.class);
                startActivity(intent);
            }
        });
    }

    public void searchList(String text){
        ArrayList<Medicines> searchList = new ArrayList<>();
        for (Medicines medicines : dataList){
            if (medicines.getName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(medicines);
            }
        }
        Log.d("ListaMedicamentos", "Número de medicamentos após a pesquisa: " + searchList.size());
        adapter.searchDataList(searchList);
    }
}

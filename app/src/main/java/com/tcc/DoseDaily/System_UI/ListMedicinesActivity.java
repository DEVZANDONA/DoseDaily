package com.tcc.DoseDaily.System_UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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

    private DrawerLayout drawerLayout;
    private SideBar sideBar;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(ListMedicinesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        drawerLayout = findViewById(R.id.drawer_layout2);
        navigationView = findViewById(R.id.nav_view2);

        sideBar = new SideBar();
        sideBar.setupDrawer(this, drawerLayout, navigationView, userId);

        ImageView sideIcon = findViewById(R.id.side_ic);
        sideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        userId = user.getUid();

        userMedicamentoQuery = FirebaseDatabase.getInstance().getReference("Medicamento")
                .orderByChild("userId").equalTo(userId);

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
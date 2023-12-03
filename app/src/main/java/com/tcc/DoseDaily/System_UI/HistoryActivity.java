package com.tcc.DoseDaily.System_UI;

import android.os.Bundle;
import android.util.Log;
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


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();


        sideBar = new SideBar();
        sideBar.setupDrawer(this, drawerLayout, navigationView, userId);


        ImageView sideIcon = findViewById(R.id.side_ic);
        sideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        Button btnLimparHistorico = findViewById(R.id.btnLimparHistorico);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        recyclerView.addItemDecoration(new DayDividerItemDecoration(this, getResources().getDimensionPixelSize(R.dimen.divider_height)));

        historicAdapter = new HistoricAdapter(itemList);
        recyclerView.setAdapter(historicAdapter);


        historicoReference = FirebaseDatabase.getInstance().getReference("Histórico");


        loadHistoryData();


        btnLimparHistorico.setOnClickListener(view -> clearHistory());
    }

    private String traduzirDiaDaSemana(String diaEmIngles) {
        switch (diaEmIngles) {
            case "Monday":
                return "Segunda-Feira";
            case "Tuesday":
                return "Terça-Feira";
            case "Wednesday":
                return "Quarta-Feira";
            case "Thursday":
                return "Quinta-Feira";
            case "Friday":
                return "Sexta-Feira";
            case "Saturday":
                return "Sábado";
            case "Sunday":
                return "Domingo";
            default:
                return diaEmIngles;
        }
    }

    private void loadHistoryData() {
        historicoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();


                String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

                for (String dayOfWeek : daysOfWeek) {
                    HistoricAdapter.SectionItem sectionItem = new HistoricAdapter.SectionItem(traduzirDiaDaSemana(dayOfWeek));

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HistoryItem historyItem = snapshot.getValue(HistoryItem.class);

                        if (historyItem != null && historyItem.getDiaDaSemana().equals(dayOfWeek)) {

                            sectionItem.addContentItem(new HistoricAdapter.ContentItem(historyItem));
                        }
                    }

                    itemList.add(sectionItem);
                }

                historicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HistoricAdapter", "Erro ao recuperar dados: " + databaseError.getMessage());
            }
        });
    }


    private void clearHistory() {
        historicoReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        historicAdapter.notifyDataSetChanged();
                    }
                });
    }
}

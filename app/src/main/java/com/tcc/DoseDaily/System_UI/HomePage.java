package com.tcc.DoseDaily.System_UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.tcc.DoseDaily.Auth.LoginActivity;
import com.tcc.DoseDaily.R;

public class HomePage extends AppCompatActivity {

    private CardView cardPerfil;
    private CardView cardMedicamentos;
    private CardView cardHistorico;
    private CardView cardReminders;
    private String userId;
    private DrawerLayout drawerLayout;
    private SideBar sideBar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        IniciarComponentes();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId");
        } else {
            FirebaseAuth.getInstance().signOut();
            Intent loginIntent = new Intent(HomePage.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

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
        cardPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        cardMedicamentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, ListMedicinesActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        cardReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, RemindersActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        cardHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(HomePage.this, HistoryActivity.class);
                startActivity(historyIntent);
            }
        });
    }

    public void IniciarComponentes() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        cardPerfil = findViewById(R.id.card_videos);
        cardMedicamentos = findViewById(R.id.card_investimentos);
        cardHistorico = findViewById(R.id.card_noticias);
        cardReminders = findViewById(R.id.card_musicas);
    }
}

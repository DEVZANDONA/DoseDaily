package com.tcc.DoseDaily.System_UI;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView; // Importe a classe CardView do pacote correto

import com.google.firebase.auth.FirebaseAuth;
import com.tcc.DoseDaily.Auth.LoginActivity;
import com.tcc.DoseDaily.R;

public class HomePage extends AppCompatActivity {

    private CardView cardPerfil;
    private CardView cardMedicamentos;
    private CardView cardNoticias;
    private CardView cardReminders;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        IniciarComponentes();


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId");
        } else {
            // Se o ID do usuário não estiver presente, faça o logout e retorne à tela de login
            FirebaseAuth.getInstance().signOut();
            Intent loginIntent = new Intent(HomePage.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        cardPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                intent.putExtra("userId", userId); // Passe o ID do usuário para a tela de perfil
                startActivity(intent);
            }
        });

        cardMedicamentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, ListMedicinesActivity.class);
                intent.putExtra("userId", userId); // Passe o ID do usuário para a lista de medicamentos
                startActivity(intent);
            }
        });

        cardReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, RemindersActivity.class);
                intent.putExtra("userId", userId); // Passe o ID do usuário para a lista de medicamentos
                startActivity(intent);
            }
        });


    }

    public void IniciarComponentes() {
        cardPerfil = findViewById(R.id.card_videos);
        cardMedicamentos = findViewById(R.id.card_investimentos);
        cardNoticias = findViewById(R.id.card_noticias);
        cardReminders = findViewById(R.id.card_musicas);
    }
}

package com.tcc.tela_login_tela_cadastro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;


public class FormLogin extends AppCompatActivity {

    private Button button_cadastro;
    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private ProgressBar progressBar;
    String[] mensagens = {"Preencha todos os campos"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        IniciarComponentes();

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d("FCM Token", token);
                        // Agora você pode usar o token conforme necessário
                    } else {
                        Log.e("FCM Token", "Falha ao obter o token do FCM");
                    }
                });

        button_cadastro.setOnClickListener(view -> {
            Intent intent = new Intent(FormLogin.this, FormCadastro.class);
            startActivity(intent);
        });

        bt_entrar.setOnClickListener(view -> {
            String email = edit_email.getText().toString().trim();
            String senha = edit_senha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                exibirMensagem("Preencha todos os campos.");
            } else {
                AuthenticarUsuario(email, senha);
            }
        });
    }


    private void AuthenticarUsuario(String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(this::TelaPrincipal, 3000);
                    } else {
                        exibirMensagem("Erro ao logar usuário.");
                    }
                });
    }

    private void TelaPrincipal() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = new Intent(FormLogin.this, HomePage.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    private void exibirMensagem(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void IniciarComponentes() {
        button_cadastro = findViewById(R.id.login_cadastro);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.senha);
        bt_entrar = findViewById(R.id.Button_Entrar);
        progressBar = findViewById(R.id.progressbar);
    }
}

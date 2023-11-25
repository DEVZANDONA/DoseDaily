package com.tcc.DoseDaily.Auth;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tcc.DoseDaily.System_UI.HomePage;
import com.tcc.DoseDaily.R;

public class LoginActivity extends AppCompatActivity {

    private Button buttonCadastro;
    private EditText editEmail, editSenha;
    private Button btEntrar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        iniciarComponentes();

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d("FCM Token", token);
                    } else {
                        Log.e("FCM Token", "Falha ao obter o token do FCM");
                    }
                });

        buttonCadastro.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btEntrar.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String senha = editSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                exibirMensagem("Preencha todos os campos.");
            } else {
                autenticarUsuario(email, senha);
            }
        });
    }

    private void autenticarUsuario(String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        iniciarAnimacao();
                        new Handler().postDelayed(this::telaPrincipal, 3000);
                    } else {
                        exibirMensagem("Erro ao logar usuário.");
                    }
                });
    }

    private void telaPrincipal() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = new Intent(LoginActivity.this, HomePage.class);
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

    private void iniciarComponentes() {
        buttonCadastro = findViewById(R.id.login_cadastro);
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.senha);
        btEntrar = findViewById(R.id.Button_Entrar);
        progressBar = findViewById(R.id.progressbar);
    }

    private void iniciarAnimacao() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f);
        anim.setDuration(1000);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();

        progressBar.setVisibility(View.VISIBLE);
    }
}
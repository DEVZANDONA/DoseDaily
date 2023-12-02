package com.tcc.DoseDaily.System_UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.tcc.DoseDaily.Auth.LoginActivity;
import com.tcc.DoseDaily.R;

public class ProfileActivity extends AppCompatActivity  {

    private TextView nomeUsuario, emailUsuario;
    private Button bt_deslogar;
    private TextView excluirConta;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;
    private Switch switchNotificacao;

    private String userId;

    private DrawerLayout drawerLayout;
    private SideBar sideBar;

    private NavigationView navigationView;
    private static final String SWITCH_STATE_PREF = "switch_state_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil);

        IniciarComponentes();


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

        ImageView iconEdit = findViewById(R.id.icon_edit);
        iconEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog();
            }
        });

        AppCompatButton editDadosButton = findViewById(R.id.edit_dados);
        editDadosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditOptionsDialog();
            }
        });


        boolean switchState = getSwitchState();
        switchNotificacao.setChecked(switchState);

        switchNotificacao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSwitchState(isChecked);
            }
        });

        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        excluirConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirDialogConfirmacao();
            }
        });
    }


    private void openEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_edit_dialog, null);
        builder.setView(view);
        EditText editNome = view.findViewById(R.id.edit_nomee);
        Button salvarButton = view.findViewById(R.id.salvar_button);
        salvarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String novoNome = editNome.getText().toString().trim();
                if (!novoNome.isEmpty()) {
                    atualizarNomeUsuario(novoNome);
                    builder.create().dismiss();
                } else {
                    Toast.makeText(ProfileActivity.this, "Digite um novo nome", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Você deseja editar quais dados?");
        builder.setItems(new CharSequence[]{"EMAIL", "SENHA"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openEditEmailDialog();
                        break;
                    case 1:
                        openEditPasswordDialog();
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        dialog.show();
    }


    private void openEditEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_edit_dialog_email, null);
        builder.setView(view);

        EditText editEmail = view.findViewById(R.id.edit_email);
        Button salvarEmailButton = view.findViewById(R.id.salvar_email_button);


        salvarEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String novoEmail = editEmail.getText().toString().trim();

                if (!novoEmail.isEmpty()) {
                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(novoEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(ProfileActivity.this, "E-mail atualizado com sucesso", Toast.LENGTH_SHORT).show();
                                    } else {

                                        Toast.makeText(ProfileActivity.this, "Erro ao atualizar e-mail", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    builder.create().dismiss();
                } else {
                    Toast.makeText(ProfileActivity.this, "Digite um novo e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openEditPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.custom_edit_dialog_senha, null);
        builder.setView(view);

        EditText editSenha = view.findViewById(R.id.edit_senha);
        EditText editConfirmacao = view.findViewById(R.id.edit_confirmacao);
        AppCompatButton salvarSenhaButton = view.findViewById(R.id.salvar_senha_button);

        salvarSenhaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String novaSenha = editSenha.getText().toString().trim();
                String confirmacaoSenha = editConfirmacao.getText().toString().trim();

                if (!novaSenha.isEmpty() && !confirmacaoSenha.isEmpty()) {

                    if (novaSenha.equals(confirmacaoSenha)) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.updatePassword(novaSenha)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Erro ao atualizar senha", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                        builder.create().dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void atualizarNomeUsuario(String novoNome) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Usuarios").document(usuarioID)
                .update("nome", novoNome)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Erro ao atualizar nome", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveSwitchState(boolean isChecked) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SWITCH_STATE_PREF, isChecked);
        editor.apply();
    }

    private boolean getSwitchState() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getBoolean(SWITCH_STATE_PREF, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nomeUsuario.setText(documentSnapshot.getString("nome"));
                    emailUsuario.setText(email);
                }
            }
        });
    }

    private void IniciarComponentes(){
        switchNotificacao = findViewById(R.id.switch2);
        nomeUsuario = findViewById(R.id.nome_user);
        emailUsuario = findViewById(R.id.email_user);
        bt_deslogar = findViewById(R.id.deslogar);
        drawerLayout = findViewById(R.id.drawer_layout4);
        navigationView = findViewById(R.id.nav_view4);
        excluirConta = findViewById(R.id.excluirContaText);
    }

    private void exibirDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Você tem certeza que deseja excluir sua conta?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        excluirContaUsuario();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        dialog.show();
    }


    private void excluirContaUsuario() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Usuarios").document(usuarioID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Erro ao excluir conta", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Erro ao excluir conta", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

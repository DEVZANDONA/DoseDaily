package com.tcc.DoseDaily.System_UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.tcc.DoseDaily.Auth.LoginActivity;
import com.tcc.DoseDaily.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView nomeUsuario, emailUsuario;
    private Button bt_deslogar;
    private TextView excluirConta;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;
    private Switch switchNotificacao;
    private static final String SWITCH_STATE_PREF = "switch_state_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil);

        IniciarComponentes();

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

        // Carrega o estado salvo do Switch
        boolean switchState = getSwitchState();
        switchNotificacao.setChecked(switchState);

        // Define um ouvinte para mudanças no estado do Switch
        switchNotificacao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Salva o estado atual do Switch
                saveSwitchState(isChecked);
                // Se o Switch estiver ligado, você pode fazer algo aqui
                // Se estiver desligado, você pode fazer algo diferente
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

        // Adicione este trecho para lidar com o clique no botão "Excluir Conta"
        excluirConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirDialogConfirmacao();
            }
        });
    }

    private void openEditDialog() {
        // Cria o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Infla o layout personalizado
        View view = getLayoutInflater().inflate(R.layout.custom_edit_dialog, null);
        builder.setView(view);

        // Obtenha a referência para os componentes do layout personalizado
        EditText editNome = view.findViewById(R.id.edit_nomee);
        Button salvarButton = view.findViewById(R.id.salvar_button);

        // Adicione um listener ao botão Salvar
        salvarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui você pode salvar o novo nome no Firestore
                String novoNome = editNome.getText().toString().trim();
                if (!novoNome.isEmpty()) {
                    // Atualize o nome no Firestore
                    atualizarNomeUsuario(novoNome);
                    // Feche o diálogo
                    builder.create().dismiss();
                } else {
                    Toast.makeText(ProfileActivity.this, "Digite um novo nome", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cria e exibe o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Você deseja editar quais dados?");
        builder.setItems(new CharSequence[]{"EMAIL", "SENHA"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // "which" representa o índice do item selecionado (EMAIL = 0, SENHA = 1)
                switch (which) {
                    case 0:
                        // Usuário escolheu editar o EMAIL
                        // Chama o diálogo de edição de e-mail
                        openEditEmailDialog();
                        break;
                    case 1:
                        // Usuário escolheu editar a SENHA
                        openEditPasswordDialog();
                        break;
                }
            }
        });

        builder.create().show();
    }

    private void openEditEmailDialog() {
        // Cria o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Infla o layout personalizado
        View view = getLayoutInflater().inflate(R.layout.custom_edit_dialog_email, null);
        builder.setView(view);

        // Obtenha a referência para os componentes do layout personalizado
        EditText editEmail = view.findViewById(R.id.edit_email);
        Button salvarEmailButton = view.findViewById(R.id.salvar_email_button);

        // Adicione um listener ao botão Salvar
        salvarEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui você pode obter o novo e-mail e fazer as alterações necessárias
                String novoEmail = editEmail.getText().toString().trim();

                if (!novoEmail.isEmpty()) {
                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(novoEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Se a atualização do e-mail for bem-sucedida, você pode realizar
                                        // outras operações necessárias, se houver, e exibir uma mensagem
                                        Toast.makeText(ProfileActivity.this, "E-mail atualizado com sucesso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Se houver um problema ao atualizar o e-mail
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

        // Cria e exibe o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openEditPasswordDialog() {
        // Cria o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Infla o layout personalizado
        View view = getLayoutInflater().inflate(R.layout.custom_edit_dialog_senha, null);
        builder.setView(view);

        // Obtenha a referência para os componentes do layout personalizado
        EditText editSenha = view.findViewById(R.id.edit_senha);
        EditText editConfirmacao = view.findViewById(R.id.edit_confirmacao);
        AppCompatButton salvarSenhaButton = view.findViewById(R.id.salvar_senha_button);

        // Adicione um listener ao botão Salvar
        salvarSenhaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui você pode obter a nova senha e confirmação e fazer as alterações necessárias
                String novaSenha = editSenha.getText().toString().trim();
                String confirmacaoSenha = editConfirmacao.getText().toString().trim();

                if (!novaSenha.isEmpty() && !confirmacaoSenha.isEmpty()) {
                    // Verifique se as senhas coincidem
                    if (novaSenha.equals(confirmacaoSenha)) {
                        // Adicione a lógica para modificar a senha no Firebase Authentication aqui
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.updatePassword(novaSenha)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show();
                                                // Faça outras operações ou lógicas, se necessário
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Erro ao atualizar senha", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        // Depois de fazer as alterações necessárias, você pode fechar o diálogo
                        builder.create().dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cria e exibe o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void atualizarNomeUsuario(String novoNome) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Atualize o nome no Firestore
        db.collection("Usuarios").document(usuarioID)
                .update("nome", novoNome)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Nome atualizado com sucesso
                        Toast.makeText(ProfileActivity.this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Se houver um problema ao atualizar o nome
                        Toast.makeText(ProfileActivity.this, "Erro ao atualizar nome", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Adicione estes métodos para salvar e obter o estado do Switch no SharedPreferences
    private void saveSwitchState(boolean isChecked) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SWITCH_STATE_PREF, isChecked);
        editor.apply();
    }

    private boolean getSwitchState() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        // Obtém o estado atual do Switch, o valor padrão é true (ligado)
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
        excluirConta = findViewById(R.id.excluirContaText); // Adicione esta linha para inicializar o ImageView
    }

    // Adicione este método para exibir o diálogo de confirmação
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
                        // Nada acontece, usuário optou por não excluir
                    }
                });
        builder.create().show();
    }

    // Adicione este método para lidar com a exclusão da conta
    private void excluirContaUsuario() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Exclui do Firestore
        db.collection("Usuarios").document(usuarioID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Agora, exclua o usuário da autenticação
                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Usuário excluído com sucesso
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Se houver um problema ao excluir o usuário da autenticação
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

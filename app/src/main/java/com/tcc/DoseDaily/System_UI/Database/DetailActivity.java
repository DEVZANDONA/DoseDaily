package com.tcc.DoseDaily.System_UI.Database;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tcc.DoseDaily.Models.HistoryItem;
import com.tcc.DoseDaily.R;
import com.tcc.DoseDaily.System_UI.HistoryActivity;
import com.tcc.DoseDaily.System_UI.ListMedicinesActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DetailActivity extends AppCompatActivity {
    FloatingActionButton editButton, deleteButton;
    TextView  title,edit_dosagem, edit_descricao;
    String imageUrl, key, medicamentoId;
    View btNotificacao,btConsumir;
    ImageView bt_riscos;
    String userId;

    DatabaseReference medicamentoReference;
    DatabaseReference historicoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        historicoReference = FirebaseDatabase.getInstance().getReference("Histórico");  // Adicione esta linha
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        medicamentoReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        bt_riscos = findViewById(R.id.bt_riscos);
        edit_descricao = findViewById(R.id.edit_descricao);
        title = findViewById(R.id.title);
        edit_dosagem = findViewById(R.id.edit_dosagem);
        btConsumir = findViewById(R.id.bt_consumir);
        FloatingActionButton editButton = findViewById(R.id.editButton);
        FloatingActionButton deleteButton = findViewById(R.id.deleteButton);
        btNotificacao = findViewById(R.id.bt_notificacao);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            edit_descricao.setText(bundle.getString("Description"));
            title.setText(bundle.getString("Title"));
            edit_dosagem.setText(bundle.getString("Language"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            medicamentoId = bundle.getString("MedicamentoId");
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUpdateActivity();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });

        btNotificacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obter a data atual
                Calendar calendar = Calendar.getInstance();
                int anoAtual = calendar.get(Calendar.YEAR);
                int mesAtual = calendar.get(Calendar.MONTH);
                int diaAtual = calendar.get(Calendar.DAY_OF_MONTH);

                // Criar um DatePickerDialog para permitir que o usuário escolha a data
                DatePickerDialog datePickerDialog = new DatePickerDialog(DetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Salvar o ano, mês e dia escolhidos pelo usuário em variáveis
                                int anoEscolhido = year;
                                int mesEscolhido = month; // O mês começa do zero
                                int diaEscolhido = dayOfMonth;

                                // Obter a hora atual
                                Calendar calendar = Calendar.getInstance();
                                int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
                                int minutoAtual = calendar.get(Calendar.MINUTE);


                                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                int horaEscolhida = hourOfDay;
                                                int minutosEscolhidos = minute;


                                                String dataFormatada = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d",
                                                        anoEscolhido, mesEscolhido + 1, diaEscolhido, horaEscolhida, minutosEscolhidos);


                                                String tituloMedicamento = title.getText().toString();
                                                String descricaoMedicamento = edit_descricao.getText().toString();


                                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                                                builder.setTitle("Escolha a Frequência");

                                                // Opções de frequência
                                                String[] opcoesFrequencia = {"Nenhum", "Diariamente", "Semanalmente", "Mensalmente"};

                                                builder.setItems(opcoesFrequencia, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String frequenciaEscolhida = opcoesFrequencia[which];

                                                        // Obter o deviceToken do usuário usando Firebase Messaging
                                                        FirebaseMessaging.getInstance().getToken()
                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<String> task) {
                                                                        if (task.isSuccessful() && task.getResult() != null) {
                                                                            String deviceToken = task.getResult();

                                                                            // Obter uma referência ao nó "Medicamento" no RealTimeDatabase
                                                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");

                                                                            // Gerar uma chave única para o novo medicamento
                                                                            String medicamentoKey = databaseReference.push().getKey();

                                                                            // Criar um mapa com os dados do medicamento
                                                                            Map<String, Object> medicamentoData = new HashMap<>();
                                                                            medicamentoData.put("titulo", tituloMedicamento);
                                                                            medicamentoData.put("corpo", descricaoMedicamento);
                                                                            medicamentoData.put("tempoNotificacao", dataFormatada);
                                                                            medicamentoData.put("deviceToken", deviceToken);
                                                                            medicamentoData.put("frequencia", frequenciaEscolhida); // Adicionar a frequência ao mapa

                                                                            // Enviar os dados para o RealTimeDatabase
                                                                            databaseReference.child(medicamentoKey).setValue(medicamentoData)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Toast.makeText(DetailActivity.this, "Notificação agendada e dados armazenados!", Toast.LENGTH_SHORT).show();
                                                                                            } else {
                                                                                                Toast.makeText(DetailActivity.this, "Falha ao agendar a notificação. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        } else {
                                                                            Toast.makeText(DetailActivity.this, "Falha ao obter o token do dispositivo. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });

                                                builder.show(); // Exibir o AlertDialog
                                            }
                                        }, horaAtual, minutoAtual, true);
                                timePickerDialog.show(); // Exibir o TimePickerDialog para o usuário
                            }
                        }, anoAtual, mesAtual, diaAtual); // ano, mes e dia são as variáveis que representam a data atual

                datePickerDialog.show(); // Exibir o DatePickerDialog para o usuário
            }
        });

        btConsumir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnConsumir = (Button) btConsumir;
                String textoAtual = btnConsumir.getText().toString();

                // Obter a hora atual
                Calendar calendar = Calendar.getInstance();
                int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
                int minutoAtual = calendar.get(Calendar.MINUTE);

                // Formatar a hora como "HH:mm"
                String horaFormatada = String.format(Locale.getDefault(), "%02d:%02d", horaAtual, minutoAtual);

                // Obter o dia do sistema
                int diaAtual = calendar.get(Calendar.DAY_OF_MONTH);

                // Obter o título do medicamento
                String tituloMedicamento = title.getText().toString();

                // Obter o dia da semana atual
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
                Date dataAtual = calendar.getTime();
                String diaSemana = sdf.format(dataAtual);

                // Criar uma instância de HistoryItem
                HistoryItem historyItem = new HistoryItem(tituloMedicamento, horaFormatada, diaAtual, diaSemana);

                // Obter uma referência ao nó "Histórico" no RealTimeDatabase
                DatabaseReference historicoReference = FirebaseDatabase.getInstance().getReference("Histórico");

                // Adicionar os dados do histórico ao nó "Histórico"
                historicoReference.push().setValue(historyItem)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DetailActivity.this, "Histórico registrado com sucesso!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DetailActivity.this, "Falha ao registrar histórico. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                // Criar um Intent para abrir a HistoryActivity
                Intent historyIntent = new Intent(DetailActivity.this, HistoryActivity.class);
                // Passar os dados como extras para a HistoryActivity
                historyIntent.putExtra("medicationName", tituloMedicamento);
                historyIntent.putExtra("consumptionTime", horaFormatada);
                historyIntent.putExtra("consumptionDay", diaAtual);
                // Iniciar a HistoryActivity
                startActivity(historyIntent);
            }
        });

        bt_riscos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Criar e exibir um AlertDialog com a mensagem fornecida
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("De acordo com o Ministério da Saúde:");
                builder.setMessage("A automedicação, muitas vezes vista como uma solução para o alívio imediato de alguns sintomas, pode trazer consequências mais graves do que se imagina.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Opcional: adicionar lógica adicional ao clicar em "OK"
                    }
                });

                // Exibir o AlertDialog
                builder.show();
            }
        });

    }

        private void navigateToUpdateActivity() {
        Intent intent = new Intent(DetailActivity.this, UpdateMedicineActivity.class)
                .putExtra("Title", title.getText().toString())
                .putExtra("Description", edit_descricao.getText().toString())
                .putExtra("Language", edit_dosagem.getText().toString())
                .putExtra("Image", imageUrl)
                .putExtra("Key", key);
        startActivity(intent);
    }

    private void deleteItem() {
        medicamentoReference.child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailActivity.this, "Excluído", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ListMedicinesActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailActivity.this, "Erro ao excluir medicamento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

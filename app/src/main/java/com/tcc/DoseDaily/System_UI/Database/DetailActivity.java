package com.tcc.DoseDaily.System_UI.Database;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
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

    ImageView detailImage;

    DatabaseReference medicamentoReference;
    DatabaseReference historicoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        historicoReference = FirebaseDatabase.getInstance().getReference("Histórico");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        medicamentoReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        detailImage = findViewById(R.id.detailImage);
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

            Glide.with(this).load(imageUrl).into(detailImage);
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(DetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                int anoEscolhido = year;
                                int mesEscolhido = month;
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


                                                String[] opcoesFrequencia = {"Nenhum", "Diariamente", "Semanalmente", "Mensalmente"};

                                                builder.setItems(opcoesFrequencia, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String frequenciaEscolhida = opcoesFrequencia[which];


                                                        FirebaseMessaging.getInstance().getToken()
                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<String> task) {
                                                                        if (task.isSuccessful() && task.getResult() != null) {
                                                                            String deviceToken = task.getResult();


                                                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");


                                                                            String medicamentoKey = databaseReference.push().getKey();


                                                                            Map<String, Object> medicamentoData = new HashMap<>();
                                                                            medicamentoData.put("titulo", tituloMedicamento);
                                                                            medicamentoData.put("corpo", descricaoMedicamento);
                                                                            medicamentoData.put("tempoNotificacao", dataFormatada);
                                                                            medicamentoData.put("deviceToken", deviceToken);
                                                                            medicamentoData.put("frequencia", frequenciaEscolhida); // Adicionar a frequência ao mapa


                                                                            databaseReference.child(medicamentoKey).setValue(medicamentoData)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {

                                                                                            } else {

                                                                                            }
                                                                                        }
                                                                                    });
                                                                        } else {

                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });

                                                builder.show();
                                            }
                                        }, horaAtual, minutoAtual, true);
                                timePickerDialog.show();
                            }
                        }, anoAtual, mesAtual, diaAtual);

                datePickerDialog.show();
            }
        });

        btConsumir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnConsumir = (Button) btConsumir;
                String textoAtual = btnConsumir.getText().toString();


                Calendar calendar = Calendar.getInstance();
                int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
                int minutoAtual = calendar.get(Calendar.MINUTE);


                String horaFormatada = String.format(Locale.getDefault(), "%02d:%02d", horaAtual, minutoAtual);


                int diaAtual = calendar.get(Calendar.DAY_OF_MONTH);


                String tituloMedicamento = title.getText().toString();


                SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
                Date dataAtual = calendar.getTime();
                String diaSemana = sdf.format(dataAtual);


                HistoryItem historyItem = new HistoryItem(tituloMedicamento, horaFormatada, diaAtual, diaSemana);


                DatabaseReference historicoReference = FirebaseDatabase.getInstance().getReference("Histórico");


                historicoReference.push().setValue(historyItem)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {

                                }
                            }
                        });


                Intent historyIntent = new Intent(DetailActivity.this, HistoryActivity.class);

                historyIntent.putExtra("medicationName", tituloMedicamento);
                historyIntent.putExtra("consumptionTime", horaFormatada);
                historyIntent.putExtra("consumptionDay", diaAtual);

                startActivity(historyIntent);
            }
        });

        bt_riscos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);


                builder.setMessage("A automedicação, muitas vezes vista como uma solução para o alívio imediato de alguns sintomas, pode trazer consequências mais graves do que se imagina.");


                TextView titleTextView = new TextView(DetailActivity.this);
                titleTextView.setText("De acordo com o Ministério da Saúde:");
                titleTextView.setTextSize(20);
                titleTextView.setTypeface(null, Typeface.BOLD);
                titleTextView.setGravity(Gravity.CENTER);
                int paddingTop = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp);
                titleTextView.setPadding(0, paddingTop, 0, 0);


                builder.setCustomTitle(titleTextView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
                dialog.show();
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

                        startActivity(new Intent(getApplicationContext(), ListMedicinesActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}

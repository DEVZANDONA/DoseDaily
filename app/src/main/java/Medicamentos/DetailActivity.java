package Medicamentos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.tcc.tela_login_tela_cadastro.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DetailActivity extends AppCompatActivity {
    ImageView detailImage;
    FloatingActionButton editButton, deleteButton;
    TextView detailDesc, detailTitle, detailLang;
    String imageUrl, key, medicamentoId;
    Button btNotificacao;
    String userId;

    DatabaseReference medicamentoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        medicamentoReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        detailImage = findViewById(R.id.detailImage);
        com.github.clans.fab.FloatingActionButton editButton = findViewById(R.id.editButton);
        com.github.clans.fab.FloatingActionButton deleteButton = findViewById(R.id.deleteButton);
        btNotificacao = findViewById(R.id.bt_notificacao);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("Language"));
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

                                // Criar um TimePickerDialog para permitir que o usuário escolha a hora
                                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // Salvar a hora e os minutos escolhidos pelo usuário em variáveis
                                                int horaEscolhida = hourOfDay;
                                                int minutosEscolhidos = minute;

                                                // Formatar a data como "ano-mes-dia hora:minuto"
                                                String dataFormatada = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d",
                                                        anoEscolhido, mesEscolhido + 1, diaEscolhido, horaEscolhida, minutosEscolhidos);

                                                // Obter o título e a descrição do medicamento
                                                String tituloMedicamento = detailTitle.getText().toString();
                                                String descricaoMedicamento = detailDesc.getText().toString();

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
                                                                    medicamentoData.put("deviceToken", deviceToken); // Adicionar o deviceToken ao mapa

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
                                        }, horaAtual, minutoAtual, true);
                                timePickerDialog.show(); // Exibir o TimePickerDialog para o usuário
                            }
                        }, anoAtual, mesAtual, diaAtual); // ano, mes e dia são as variáveis que representam a data atual

                datePickerDialog.show(); // Exibir o DatePickerDialog para o usuário
            }
        });
    }

    private void navigateToUpdateActivity() {
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                .putExtra("Title", detailTitle.getText().toString())
                .putExtra("Description", detailDesc.getText().toString())
                .putExtra("Language", detailLang.getText().toString())
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
                        startActivity(new Intent(getApplicationContext(), ListaMedicamentos.class));
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

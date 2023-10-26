package Medicamentos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionButton;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tcc.tela_login_tela_cadastro.R;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {
    ImageView detailImage;
    FloatingActionButton editButton, deleteButton;

    TextView detailDesc, detailTitle, detailLang;
    String imageUrl, key;
    Button btNotificacao;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference lembretesReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId).child("Lembretes");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        detailImage = findViewById(R.id.detailImage);
        editButton = (FloatingActionButton) findViewById(R.id.editButton);
        deleteButton = (FloatingActionButton) findViewById(R.id.deleteButton);
        btNotificacao = findViewById(R.id.bt_notificacao);
        lembretesReference = FirebaseDatabase.getInstance().getReference("lembretes");


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("Language"));
            key = bundle.getString("Key");  // A chave única do item
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
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
                Calendar calendar = Calendar.getInstance();
                int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
                int minutoAtual = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                DatePickerDialog datePickerDialog = new DatePickerDialog(DetailActivity.this,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                long delayInMillis = calcularDelayParaNotificacao(year, month, dayOfMonth, hourOfDay, minute);
                                                scheduleNotification(delayInMillis, "Título do lembrete", "Descrição do lembrete");
                                                Toast.makeText(DetailActivity.this, "Notificação agendada com sucesso!", Toast.LENGTH_SHORT).show();
                                            }
                                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                                datePickerDialog.show();
                            }
                        }, horaAtual, minutoAtual, true);
                timePickerDialog.show();
            }
        });
    }

    private long calcularDelayParaNotificacao(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        Calendar notificationCalendar = Calendar.getInstance();
        notificationCalendar.set(Calendar.YEAR, year);
        notificationCalendar.set(Calendar.MONTH, month);
        notificationCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        notificationCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        notificationCalendar.set(Calendar.MINUTE, minute);
        notificationCalendar.set(Calendar.SECOND, 0);

        long notificationTimeMillis = notificationCalendar.getTimeInMillis();
        long currentTimeMillis = System.currentTimeMillis();

        // Se o horário da notificação já passou para hoje, agende para amanhã
        if (notificationTimeMillis <= currentTimeMillis) {
            notificationTimeMillis += AlarmManager.INTERVAL_DAY;
        }

        // Calcule o atraso até o horário da notificação em milissegundos
        return notificationTimeMillis - currentTimeMillis;
    }

    private void scheduleNotification(long delayInMillis, String title, String description) {
        Intent intent = new Intent(DetailActivity.this, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMillis, pendingIntent);
    }

    private void adicionarLembreteAoFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtendo os detalhes do lembrete
        String titulo = "Título do lembrete";
        String descricao = "Descrição do lembrete";

        // Criando um objeto de lembrete com os detalhes
        Lembrete lembrete = new Lembrete(titulo, descricao, userId);

        // Obtendo a referência ao nó Medicamento no Firebase
        DatabaseReference medicamentoReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        // Adicionando o lembrete ao nó Medicamento no Firebase
        DatabaseReference userLembretesReference = medicamentoReference.child(key).child("Lembretes");
        userLembretesReference.push().setValue(lembrete)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Lembrete adicionado com sucesso
                        Toast.makeText(DetailActivity.this, "Lembrete adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Trate falhas ao adicionar o lembrete
                        Toast.makeText(DetailActivity.this, "Erro ao adicionar lembrete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void navigateToUpdateActivity() {
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                .putExtra("Title", detailTitle.getText().toString())
                .putExtra("Description", detailDesc.getText().toString())
                .putExtra("Language", detailLang.getText().toString())
                .putExtra("Image", imageUrl)
                .putExtra("Key", key);  // Passe a chave única
        startActivity(intent);
    }
    private long calcularDelayParaNotificacao() {
        int horas = 8; // Horas
        int minutos = 0; // Minutos

        // Obtenha o tempo atual em milissegundos
        long currentTimeMillis = System.currentTimeMillis();

        // Crie um objeto Calendar com o horário da notificação
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, horas);
        calendar.set(Calendar.MINUTE, minutos);
        calendar.set(Calendar.SECOND, 0);

        // Obtenha o tempo da notificação em milissegundos
        long notificationTimeMillis = calendar.getTimeInMillis();

        // Se o horário da notificação já passou para hoje, agende para amanhã
        if (notificationTimeMillis <= currentTimeMillis) {
            notificationTimeMillis += AlarmManager.INTERVAL_DAY;
        }

        // Calcule o atraso até o horário da notificação em milissegundos
        return notificationTimeMillis - currentTimeMillis;
    }

    private void deleteItem() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Medicamento");

        if (!imageUrl.equals("") && !imageUrl.equals("https://firebasestorage.googleapis.com/v0/b/dosedaily-4dd77.appspot.com/o/Medicamento%20Imagens%2Fdefault_image.jpg?alt=media&token=7158d78e-fc26-4b72-bbc9-9cf2c2455b52&_gl=1*1kabngz*_ga*MTgwNTM1MzkzMC4xNjk0MTkxMjEy*_ga_CW55HF8NVT*MTY5NjMwNDY0Ni4yNi4xLjE2OTYzMDcxNzQuNTkuMC4w")) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    deleteMedicamentoFromDatabase(reference);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailActivity.this, "Error deleting image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            deleteMedicamentoFromDatabase(reference);
        }
    }

    private void deleteMedicamentoFromDatabase(DatabaseReference reference) {
        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                displayToastAndReturnToListaMedicamentos();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Trate qualquer erro ao deletar o medicamento aqui
                Toast.makeText(DetailActivity.this, "Error deleting medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayToastAndReturnToListaMedicamentos() {
        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), ListaMedicamentos.class));
        finish();
    }
}

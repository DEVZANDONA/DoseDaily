package com.tcc.DoseDaily.System_UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tcc.DoseDaily.Adapters.NotificationsAdapter;
import com.tcc.DoseDaily.Models.Notifications;
import com.tcc.DoseDaily.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RemindersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Notifications> notificationsList;
    private List<Notifications> filteredList;
    private NotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationsList = new ArrayList<>();
        filteredList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notifications notification = snapshot.getValue(Notifications.class);
                    notificationsList.add(notification);
                }

                applySearch("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RemindersActivity.this, "Erro ao obter dados do banco de dados", Toast.LENGTH_SHORT).show();
            }
        });

        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applySearch(newText);
                return true;
            }
        });
    }

    private void applySearch(String query) {
        filteredList.clear();

        for (Notifications notification : notificationsList) {
            if (notification.getCorpo() != null && notification.getTempoNotificacao() != null && notification.getTitulo() != null) {
                if (notification.getCorpo().toLowerCase().contains(query.toLowerCase()) || notification.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(notification);
                }
            }
        }

        updateRecyclerView(filteredList);
    }

    private void updateRecyclerView(List<Notifications> filteredList) {
        if (adapter == null) {
            adapter = new NotificationsAdapter(filteredList, new NotificationsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Notifications notification) {
                    // Certifique-se de que o clique é detectado corretamente aqui
                    // (adicionar um log para verificar)
                    Log.d("RemindersActivity", "Item Clicked: " + notification.getTitulo());
                    // Adicione a lógica desejada para tratar o clique em uma notificação específica
                }

                @Override
                public void onItemClick(int position) {
                    showCustomDialog(position);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void showCustomDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Você deseja fazer alguma alteração nesta notificação?")
                .setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        modifyNotification(position);
                        Toast.makeText(RemindersActivity.this, "Notificação modificada", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Deletar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNotification(position);
                        Toast.makeText(RemindersActivity.this, "Notificação deletada", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void modifyNotification(final int position) {
        // Obter a notificação selecionada
        Notifications selectedNotification = filteredList.get(position);

        // Criar um DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Quando a data é definida, criar um TimePickerDialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                RemindersActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Quando a hora é definida, criar uma string no formato desejado
                                        String newDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d", year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);

                                        // Atualizar a notificação com o novo horário
                                        updateNotificationDateTime(position, newDateTime);
                                    }
                                },
                                0, 0, true);

                        // Mostrar o TimePickerDialog
                        timePickerDialog.show();
                    }
                },
                // Definir a data inicial para o DatePickerDialog como a data atual
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        // Mostrar o DatePickerDialog
        datePickerDialog.show();
    }

    private void updateNotificationDateTime(final int position, final String newDateTime) {
        Notifications selectedNotification = filteredList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        // Consulta no banco de dados usando os campos específicos
        Query query = databaseReference.orderByChild("corpo").equalTo(selectedNotification.getCorpo());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Itera sobre os resultados, embora deva haver apenas um
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Recuperar os dados da notificação clicada
                        Notifications notification = snapshot.getValue(Notifications.class);

                        // Modificar o tempoNotificacao
                        notification.setTempoNotificacao(newDateTime);

                        // Atualizar no banco de dados
                        snapshot.getRef().setValue(notification);

                        // Atualizar a lista e o RecyclerView se necessário
                        filteredList.set(position, notification);
                        adapter.notifyItemChanged(position);

                        Toast.makeText(RemindersActivity.this, "Notificação modificada para " + newDateTime, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RemindersActivity.this, "Erro ao modificar notificação", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotification(final int position) {
        Notifications selectedNotification = filteredList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        // Consulta no banco de dados usando os campos específicos
        Query query = databaseReference.orderByChild("corpo").equalTo(selectedNotification.getCorpo());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Itera sobre os resultados, embora deva haver apenas um
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (!filteredList.isEmpty() && position >= 0 && position < filteredList.size()) {
                                    // Remover da lista filtrada após a exclusão bem-sucedida
                                    filteredList.remove(position);
                                    // Notificar o RecyclerView sobre a mudança
                                    adapter.notifyItemRemoved(position);
                                    Toast.makeText(RemindersActivity.this, "Notificação deletada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RemindersActivity.this, "Lista vazia ou posição inválida", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(RemindersActivity.this, "Notificação não encontrada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RemindersActivity.this, "Erro ao deletar notificação", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


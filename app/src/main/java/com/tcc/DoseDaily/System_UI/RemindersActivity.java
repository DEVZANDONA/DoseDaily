package com.tcc.DoseDaily.System_UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private NotificationsAdapter adapter;
    private String userId;
    private DrawerLayout drawerLayout;
    private SideBar sideBar;
    private NavigationView navigationView;
    private List<Notifications> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationsList = new ArrayList<>();
        filteredList = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer_layout3);
        navigationView = findViewById(R.id.nav_view3);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        sideBar = new SideBar();
        sideBar.setupDrawer(this, drawerLayout, navigationView, userId);

        ImageView sideIcon = findViewById(R.id.side_ic);
        sideIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");

        adapter = new NotificationsAdapter(notificationsList, (notification, position) -> {
            showCustomDialog(position);
        });

        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notifications notification = snapshot.getValue(Notifications.class);
                    if (notification != null) {
                        notificationsList.add(notification);
                    }
                }

                applySearch("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            if (notification != null && notification.getTitulo() != null) {
                String title = notification.getTitulo().toLowerCase(Locale.getDefault());
                if (title.contains(query.toLowerCase(Locale.getDefault()))) {
                    filteredList.add(notification);
                }
            }
        }

        adapter.filterList(filteredList);
    }

    private void showCustomDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Você deseja fazer alguma alteração nesta notificação?");
        builder.setPositiveButton("Modificar", (dialog, which) -> {
            modifyNotification(position);

        });

        builder.setNegativeButton("Deletar", (dialog, which) -> {
            deleteNotification(position);

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void modifyNotification(final int position) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                String newDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d", year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);
                updateNotificationDateTime(position, newDateTime);
            }, 0, 0, true);

            timePickerDialog.show();
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void updateNotificationDateTime(final int position, final String newDateTime) {
        Notifications selectedNotification = filteredList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");
        Query query = databaseReference.orderByChild("corpo").equalTo(selectedNotification.getCorpo());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notifications notification = snapshot.getValue(Notifications.class);
                        notification.setTempoNotificacao(newDateTime);
                        snapshot.getRef().setValue(notification);

                        filteredList.set(position, notification);
                        adapter.notifyItemChanged(position);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteNotification(final int position) {
        Notifications selectedNotification = filteredList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Medicamento");
        Query query = databaseReference.orderByChild("corpo").equalTo(selectedNotification.getCorpo());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue((databaseError, databaseReference) -> {
                            if (!filteredList.isEmpty() && position >= 0 && position < filteredList.size()) {
                                filteredList.remove(position);
                                adapter.notifyItemRemoved(position);

                            } else {

                            }
                        });
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

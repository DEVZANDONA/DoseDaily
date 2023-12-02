package com.tcc.DoseDaily.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tcc.DoseDaily.System_UI.HomePage;
import com.tcc.DoseDaily.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";
    private static final String CHANNEL_ID = "channel_id";
    private static final String SWITCH_STATE_PREF = "switch_state_pref";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Verifica se a mensagem contém dados
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Mensagem de dados recebida: " + remoteMessage.getData());
            String tituloRecebido = remoteMessage.getData().get("titulo");
            String corpoRecebido = remoteMessage.getData().get("corpo");

            Log.d(TAG, "Título Recebido: " + tituloRecebido);
            Log.d(TAG, "Corpo Recebido: " + corpoRecebido);

            // Obtém o estado atual do switch
            boolean isSwitchOn = getSwitchState();

            // Se o switch estiver ligado, mostra a notificação
            if (isSwitchOn) {
                // Atualiza o título para a mensagem padrão
                String tituloPadrao = "Está na Hora de Tomar seu Remédio!!";
                // Adiciona o título e corpo da notificação recebida abaixo do título padrão
                showNotification(tituloPadrao, tituloRecebido + "\n" + corpoRecebido);
            } else {
                Log.d(TAG, "Switch desligado. Não mostrando notificação.");
            }
        }
    }

    private boolean getSwitchState() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        // Obtém o estado atual do switch, o valor padrão é true (ligado)
        return preferences.getBoolean(SWITCH_STATE_PREF, true);
    }

    private void showNotification(String title, String message) {
        Log.d(TAG, "Mostrando notificação. Título: " + title + ", Corpo: " + message);
        Intent intent = new Intent(this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_email)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, builder.build());
    }
}
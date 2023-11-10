package Medicamentos;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionRequestActivity extends Activity {

    private static final int REQUEST_CODE_NOTIFICACOES = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICACOES);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Verificar se a permissão foi concedida
        if (requestCode == REQUEST_CODE_NOTIFICACOES && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permissão concedida, você pode iniciar seu serviço FirebaseMessagingService a partir daqui
            Log.d("NotificationPermission", "Permissão concedida. Iniciar o serviço FirebaseMessagingService aqui.");
        } else {
            // Permissão negada. Explique ao usuário e forneça uma opção para concedê-la manualmente.
            Log.d("NotificationPermission", "Permissão negada. Explique ao usuário e forneça uma opção para concedê-la manualmente.");
        }

        finish();
    }
}

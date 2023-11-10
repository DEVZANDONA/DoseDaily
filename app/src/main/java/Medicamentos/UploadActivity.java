package Medicamentos;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tcc.tela_login_tela_cadastro.R;

public class UploadActivity extends AppCompatActivity {
    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic, uploadDesc, uploadLang;
    String imageURL;
    Uri uri;
    AlertDialog dialog;
    Drawable defaultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadImage = findViewById(R.id.uploadImage);
        uploadDesc = findViewById(R.id.uploadDesc);
        uploadTopic = findViewById(R.id.uploadTopic);
        uploadLang = findViewById(R.id.uploadLang);
        saveButton = findViewById(R.id.saveButton);
        defaultImage = getResources().getDrawable(R.drawable.medicine);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        uri = data.getData();
                        uploadImage.setImageURI(uri);
                    } else {
                        Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        uploadImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        saveButton.setOnClickListener(view -> {
            if (validateFields()) {
                saveData();
            } else {
                Toast.makeText(UploadActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFields() {
        return !uploadTopic.getText().toString().trim().isEmpty() &&
                !uploadDesc.getText().toString().trim().isEmpty() &&
                !uploadLang.getText().toString().trim().isEmpty();
    }

    public void saveData() {
        dialog.show();

        // Obtendo o usuário atual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Verifica se o usuário está autenticado
        if (user != null) {
            // Obtendo o ID do usuário logado
            String userId = ((FirebaseUser) user).getUid();

            if (uri == null) {
                // Se a URI for nula, simplesmente defina o URL da imagem padrão
                imageURL = "https://firebasestorage.googleapis.com/v0/b/dosedaily-4dd77.appspot.com/o/Medicamento%20Imagens%2Fdefault_image.jpg?alt=media&token=7158d78e-fc26-4b72-bbc9-9cf2c2455b52&_gl=1*1kabngz*_ga*MTgwNTM1MzkzMC4xNjk0MTkxMjEy*_ga_CW55HF8NVT*MTY5NjMwNDY0Ni4yNi4xLjE2OTYzMDcxNzQuNTkuMC4w";
                uploadData(userId);
            } else {
                // Caso contrário, faça o upload da imagem selecionada pelo usuário
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Medicamento Imagens").child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnCompleteListener(uriResult -> {
                        if (uriResult.isSuccessful()) {
                            imageURL = uriResult.getResult().toString();
                            uploadData(userId);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(UploadActivity.this, "Erro ao obter URL da imagem: " + uriResult.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Erro ao fazer upload da imagem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        } else {
            // Se o usuário não estiver autenticado, exibe uma mensagem
            dialog.dismiss();
            Toast.makeText(UploadActivity.this, "You need to be logged in to perform this action", Toast.LENGTH_SHORT).show();
        }
    }


    public void uploadData(String userId){
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String lang = uploadLang.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Medicamento").push();
        String uniqueKey = ref.getKey();

        DataClass dataClass = new DataClass(title, desc, lang, imageURL, uniqueKey, userId);

        ref.setValue(dataClass).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()){
                Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(UploadActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

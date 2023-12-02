package com.tcc.DoseDaily.System_UI.Database;

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
import com.tcc.DoseDaily.Models.Medicines;
import com.tcc.DoseDaily.R;

public class UploadMedicineActivity extends AppCompatActivity {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadMedicineActivity.this);
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
                        Toast.makeText(UploadMedicineActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UploadMedicineActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
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


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {

            String userId = ((FirebaseUser) user).getUid();

            if (uri == null) {

                imageURL = "https://firebasestorage.googleapis.com/v0/b/dosedaily-4dd77.appspot.com/o/Medicamento%20Imagens%2Fdefault_image.jpg?alt=media&token=7158d78e-fc26-4b72-bbc9-9cf2c2455b52&_gl=1*1kabngz*_ga*MTgwNTM1MzkzMC4xNjk0MTkxMjEy*_ga_CW55HF8NVT*MTY5NjMwNDY0Ni4yNi4xLjE2OTYzMDcxNzQuNTkuMC4w";
                uploadData(userId);
            } else {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Medicamento Imagens").child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnCompleteListener(uriResult -> {
                        if (uriResult.isSuccessful()) {
                            imageURL = uriResult.getResult().toString();
                            uploadData(userId);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(UploadMedicineActivity.this, "Erro ao obter URL da imagem: " + uriResult.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(UploadMedicineActivity.this, "Erro ao fazer upload da imagem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        } else {

            dialog.dismiss();
            Toast.makeText(UploadMedicineActivity.this, "You need to be logged in to perform this action", Toast.LENGTH_SHORT).show();
        }
    }


    public void uploadData(String userId){
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String lang = uploadLang.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Medicamento").push();
        String uniqueKey = ref.getKey();

        Medicines medicines = new Medicines(title, desc, lang, imageURL, uniqueKey, userId);

        ref.setValue(medicines).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()){
                Toast.makeText(UploadMedicineActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(UploadMedicineActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

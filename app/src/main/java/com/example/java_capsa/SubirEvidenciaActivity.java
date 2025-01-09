package com.example.java_capsa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SubirEvidenciaActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;
    private ImageView ivPhotoPreview;
    private Spinner spEstado;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_evidencia);

        ivPhotoPreview = findViewById(R.id.iv_photo_preview);
        spEstado = findViewById(R.id.sp_estado);
        Button btnTakePhoto = findViewById(R.id.btn_take_photo);
        Button btnSubmitEvidence = findViewById(R.id.btn_submit_evidence);

        btnTakePhoto.setOnClickListener(v -> takePhoto());
        btnSubmitEvidence.setOnClickListener(v -> submitEvidence());
    }

    private void takePhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            ivPhotoPreview.setImageBitmap(photo);
        }
    }

    private void submitEvidence() {
        if (photo == null) {
            Toast.makeText(this, "Por favor, tome una foto.", Toast.LENGTH_SHORT).show();
            return;
        }

        String estado = spEstado.getSelectedItem().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        String nombreUsuario = auth.getCurrentUser().getDisplayName();

        // Subir foto a Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("evidencias/" + userId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Guardar datos en Firestore
            guardarEvidenciaFirestore(uri.toString(), estado, nombreUsuario);
        })).addOnFailureListener(e -> Toast.makeText(this, "Error al subir foto.", Toast.LENGTH_SHORT).show());
    }

    private void guardarEvidenciaFirestore(String imageUrl, String estado, String nombreUsuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> evidencia = new HashMap<>();
        evidencia.put("nombre", nombreUsuario);
        evidencia.put("estado", estado);
        evidencia.put("fecha", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        evidencia.put("hora", new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        evidencia.put("fotoUrl", imageUrl);

        db.collection("evidencias").add(evidencia).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Evidencia enviada con éxito.", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al guardar evidencia.", Toast.LENGTH_SHORT).show());
    }
}

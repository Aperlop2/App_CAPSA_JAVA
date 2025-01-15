package com.example.java_capsa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularioCuidador extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1; // Código para identificar la solicitud de captura de imagen
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100; // Código para identificar la solicitud de permisos de cámara

    private EditText etFullName, etEmail, etPhoneNumber, etBirthDate, etSpecialty, etAvailableHours, etPassword, etConfirmPassword;
    private ImageView ivProfilePicture;
    private DatabaseReference solicitudesReference;
    private Bitmap capturedImage; // Bitmap para guardar la imagen capturada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_cuidador);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etBirthDate = findViewById(R.id.etBirthDate);
        etSpecialty = findViewById(R.id.etSpecialty);
        etAvailableHours = findViewById(R.id.etAvailableHours);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivProfilePicture = findViewById(R.id.ivProfilePicture); // Imagen del perfil
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnUploadPhoto = findViewById(R.id.btnUploadPhoto);

        solicitudesReference = FirebaseDatabase.getInstance().getReference("solicitudes_cuidadores");

        btnRegister.setOnClickListener(v -> registerCaretaker());
        btnUploadPhoto.setOnClickListener(v -> checkCameraPermission()); // Configurar botón para abrir la cámara
    }

    // Método para verificar y solicitar permisos de la cámara
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            openCamera(); // Abrir la cámara si el permiso ya fue concedido
        }
    }

    // Manejar el resultado de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para abrir la cámara
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No se pudo acceder a la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // Obtener la imagen capturada como un Bitmap
            capturedImage = (Bitmap) data.getExtras().get("data");

            // Mostrar la imagen capturada en el ImageView
            ivProfilePicture.setImageBitmap(capturedImage);
        }
    }

    private void registerCaretaker() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String specialty = etSpecialty.getText().toString().trim();
        String availableHours = etAvailableHours.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validateFields(fullName, email, phone, birthDate, specialty, availableHours, password, confirmPassword)) {
            String solicitudId = solicitudesReference.push().getKey(); // Generar un ID único para la solicitud

            // Crear un objeto de cuidador con estado pendiente
            Caretaker caretaker = new Caretaker(fullName, email, phone, birthDate, specialty, availableHours, password, "pendiente");

            // Guardar en Firebase como solicitud pendiente
            if (solicitudId != null) {
                solicitudesReference.child(solicitudId).setValue(caretaker)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Tu solicitud está pendiente de aprobación.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    private boolean validateFields(String fullName, String email, String phone, String birthDate, String specialty, String availableHours, String password, String confirmPassword) {
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(specialty) || TextUtils.isEmpty(availableHours)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}

class Caretaker {
    public String nombre, correo, telefono, fechaNacimiento, especialidad, horariosDisponibles, contraseña, estado;

    public Caretaker(String nombre, String correo, String telefono, String fechaNacimiento, String especialidad, String horariosDisponibles, String contraseña, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.especialidad = especialidad;
        this.horariosDisponibles = horariosDisponibles;
        this.contraseña = contraseña;
        this.estado = estado;
    }
}

package com.example.java_capsa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularioCuidador extends AppCompatActivity {
    private EditText etFullName, etEmail, etPhoneNumber, etBirthDate, etSpecialty, etAvailableHours, etPassword, etConfirmPassword;
    private DatabaseReference solicitudesReference;
    private FirebaseAuth auth;

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
        Button btnRegister = findViewById(R.id.btnRegister);

        solicitudesReference = FirebaseDatabase.getInstance().getReference("solicitudes_cuidadores");
        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> registerCaretaker());
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

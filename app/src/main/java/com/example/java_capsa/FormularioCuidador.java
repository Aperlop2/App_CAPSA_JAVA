package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class FormularioCuidador extends AppCompatActivity {
    private EditText etFullName, etEmail, etPhoneNumber, etBirthDate, etSpecialty, etAvailableHours, etPassword, etConfirmPassword;
    private DatabaseReference databaseReference;
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

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");
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
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                            // Hashear la contraseña antes de almacenarla
                            String hashedPassword = hashPassword(password);

                            // Crear un objeto de cuidador con la contraseña encriptada
                            Caretaker caretaker = new Caretaker(fullName, email, phone, birthDate, specialty, availableHours, hashedPassword);

                            // Guardar los datos en Firebase Realtime Database
                            databaseReference.child(userId).setValue(caretaker)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Cuidador registrado con éxito", Toast.LENGTH_SHORT).show();

                                        // Redirigir automáticamente al LoginPrincipal
                                        Intent intent = new Intent(FormularioCuidador.this, LoginPrincipal.class);
                                        startActivity(intent);
                                        finish(); // Finalizar esta actividad
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Error en el registro: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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

    private String hashPassword(String password) {
        // Generar un hash seguro con BCrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

class Caretaker {
    public String nombre, correo, telefono, fechaNacimiento, especialidad, horariosDisponibles, contraseña;

    public Caretaker(String nombre, String correo, String telefono, String fechaNacimiento, String especialidad, String horariosDisponibles, String contraseña) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.especialidad = especialidad;
        this.horariosDisponibles = horariosDisponibles;
        this.contraseña = contraseña;
    }
}

package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class FormularioAdministrador extends AppCompatActivity {
    private EditText etFullName, etEmail, etPhoneNumber, etRole, etPassword, etConfirmPassword;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_administrador);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etRole = findViewById(R.id.etRole);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios/administradores");
        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> registerAdministrator());
    }

    private void registerAdministrator() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validateFields(fullName, email, phone, role, password, confirmPassword)) {
            // Registrar en Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            String userId = user.getUid();

                            // Hashear la contraseña antes de almacenarla
                            String hashedPassword = hashPassword(password);

                            // Guardar datos adicionales en Firebase Realtime Database
                            Administrator administrator = new Administrator(fullName, email, phone, role, hashedPassword);
                            databaseReference.child(userId).setValue(administrator)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Administrador registrado con éxito", Toast.LENGTH_SHORT).show();

                                        // Redirigir automáticamente al LoginPrincipal
                                        Intent intent = new Intent(FormularioAdministrador.this, LoginPrincipal.class);
                                        startActivity(intent);
                                        finish(); // Finaliza esta actividad
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar en la base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Error al registrar el usuario: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateFields(String fullName, String email, String phone, String role, String password, String confirmPassword) {
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(role) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
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

class Administrator {
    public String nombre, correo, telefono, rol, contraseña;

    public Administrator(String nombre, String correo, String telefono, String rol, String contraseña) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
        this.contraseña = contraseña;
    }
}

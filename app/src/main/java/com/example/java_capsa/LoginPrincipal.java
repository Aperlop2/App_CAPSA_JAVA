package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

public class LoginPrincipal extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference adminRef, caretakerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_principal);

        // Inicialización de Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Referencias a Firebase Database
        adminRef = FirebaseDatabase.getInstance().getReference("usuarios/administradores");
        caretakerRef = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

        // Inicialización de vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignUp = findViewById(R.id.tvSignUp);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Manejo de eventos
        btnLogin.setOnClickListener(v -> loginUser());
        tvSignUp.setOnClickListener(v -> startActivity(new Intent(LoginPrincipal.this, RegistroAdminYCuidador.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginPrincipal.this, RecuperarContrasena.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Autenticación con Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            verificarRolUsuario(user.getUid(), email);
                        } else {
                            Toast.makeText(this, "Error al obtener la sesión del usuario", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarRolUsuario(String userId, String email) {
        // Primero, verificamos si es un Administrador
        adminRef.child(userId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Inicio de sesión exitoso como Administrador
                Toast.makeText(this, "Inicio de sesión exitoso (Administrador)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginPrincipal.this, gestionadministradores.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                // Si no es Administrador, verificamos si es Cuidador
                caretakerRef.child(userId).get().addOnSuccessListener(caretakerSnapshot -> {
                    if (caretakerSnapshot.exists()) {
                        // Inicio de sesión exitoso como Cuidador
                        Toast.makeText(this, "Inicio de sesión exitoso (Cuidador)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginPrincipal.this, DashboardCuidadorActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "No tienes permisos en la plataforma", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e ->
                        Toast.makeText(this, "Error al conectarse con Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error al conectarse con Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

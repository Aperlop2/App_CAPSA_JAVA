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

public class LoginPrincipal extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private DatabaseReference adminRef, caretakerRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_principal);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignUp = findViewById(R.id.tvSignUp);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        mAuth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference("usuarios/administradores");
        caretakerRef = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPrincipal.this, RegistroAdminYCuidador.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPrincipal.this, RecuperarContrasena.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Autenticar con Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Determinar el rol del usuario
                            determinarRolUsuario(user.getEmail());
                        }
                    } else {
                        Toast.makeText(LoginPrincipal.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void determinarRolUsuario(String email) {
        // Buscar en "administradores"
        adminRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists() && validarCorreoEnBaseDeDatos(snapshot, email)) {
                Toast.makeText(this, "Inicio de sesión exitoso (Administrador)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginPrincipal.this, gestionadministradores.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                // Si no es administrador, buscar en "cuidadores"
                caretakerRef.get().addOnSuccessListener(caretakerSnapshot -> {
                    if (caretakerSnapshot.exists() && validarCorreoEnBaseDeDatos(caretakerSnapshot, email)) {
                        Toast.makeText(this, "Inicio de sesión exitoso (Cuidador)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginPrincipal.this, DashboardCuidadorActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "No se encontró información del usuario.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al conectar con Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al conectar con Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validarCorreoEnBaseDeDatos(DataSnapshot snapshot, String email) {
        for (DataSnapshot child : snapshot.getChildren()) {
            String storedEmail = child.child("correo").getValue(String.class);
            if (storedEmail != null && storedEmail.equals(email)) {
                return true;
            }
        }
        return false;
    }
}
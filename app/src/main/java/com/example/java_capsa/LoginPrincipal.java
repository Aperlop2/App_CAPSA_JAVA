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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();  // Obtener el UID del usuario

                            // Esperar a que Firebase termine la autenticación antes de leer
                            caretakerRef.child(userId).get().addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                    // Usuario encontrado en cuidadores
                                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginPrincipal.this, DashboardCuidadorActivity.class));
                                } else {
                                    adminRef.child(userId).get().addOnCompleteListener(adminTask -> {
                                        if (adminTask.isSuccessful() && adminTask.getResult().exists()) {
                                            // Usuario encontrado en administradores
                                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginPrincipal.this, gestionadministradores.class));
                                        } else {
                                            Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

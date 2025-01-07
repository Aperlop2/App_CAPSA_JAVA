package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

public class LoginPrincipal extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private DatabaseReference adminRef, caretakerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_principal);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignUp = findViewById(R.id.tvSignUp); // Texto para redirigir al registro

        // Referencias a Firebase
        adminRef = FirebaseDatabase.getInstance().getReference("usuarios/administradores");
        caretakerRef = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

        // Manejar clic en el botón "Iniciar Sesión"
        btnLogin.setOnClickListener(v -> loginUser());

        // Manejar clic en "No tienes cuenta, regístrate aquí"
        tvSignUp.setOnClickListener(v -> {
            // Redirigir a la actividad RegistroAdminYCuidador
            Intent intent = new Intent(LoginPrincipal.this, RegistroAdminYCuidador.class);
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

        // Validar credenciales en la base de datos
        adminRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists() && validateLogin(snapshot, email, password)) {
                Toast.makeText(this, "Inicio de sesión exitoso (Administrador)", Toast.LENGTH_SHORT).show();
                // Redirigir al panel de administración
                Intent intent = new Intent(LoginPrincipal.this, gestionadministradores.class);
                startActivity(intent);
                finish(); // Finalizar actividad actual
            } else {
                caretakerRef.get().addOnSuccessListener(caretakerSnapshot -> {
                    if (caretakerSnapshot.exists() && validateLogin(caretakerSnapshot, email, password)) {
                        Toast.makeText(this, "Inicio de sesión exitoso (Cuidador)", Toast.LENGTH_SHORT).show();
                        // Aquí puedes redirigir al panel del cuidador si es necesario
                    } else {
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateLogin(DataSnapshot snapshot, String email, String password) {
        for (DataSnapshot child : snapshot.getChildren()) {
            String storedEmail = child.child("correo").getValue(String.class);
            String storedPassword = child.child("contraseña").getValue(String.class);

            if (storedEmail != null && storedPassword != null
                    && storedEmail.equals(email)
                    && BCrypt.checkpw(password, storedPassword)) {
                return true;
            }
        }
        return false;
    }
}

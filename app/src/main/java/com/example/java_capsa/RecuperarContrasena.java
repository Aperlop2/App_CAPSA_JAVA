package com.example.java_capsa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {
    private EditText etEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recuperar_contrasena);

        etEmail = findViewById(R.id.etEmailRecuperar);
        Button btnRecuperar = findViewById(R.id.btnRecuperar);
        mAuth = FirebaseAuth.getInstance();

        // Acción de recuperar contraseña
        btnRecuperar.setOnClickListener(v -> recuperarContrasena());
    }

    private void recuperarContrasena() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar correo para recuperar la contraseña
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Te hemos enviado un correo para recuperar tu contraseña", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad y regresar al Login
                    } else {
                        Toast.makeText(this, "No se pudo enviar el correo. Verifica el correo electrónico", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

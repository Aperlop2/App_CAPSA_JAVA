package com.example.java_capsa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class RecuperarContrasena extends AppCompatActivity {
    private EditText etEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recuperar_contrasena);

        // Referencia al campo de correo y botón
        etEmail = findViewById(R.id.etEmailRecuperar);
        Button btnRecuperar = findViewById(R.id.btnRecuperar);

     // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Acción al presionar el botón de recuperar contraseña
        btnRecuperar.setOnClickListener(v -> recuperarContrasena());
    }

    private void recuperarContrasena() {
        String email = etEmail.getText().toString().trim();

        // Validación del campo de correo
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar correo para recuperar contraseña
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Te hemos enviado un correo para recuperar tu contraseña", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad y regresar al Login
 } else {
                        // Manejo de errores específicos
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "El correo no está registrado en el sistema", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "El formato del correo no es válido", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

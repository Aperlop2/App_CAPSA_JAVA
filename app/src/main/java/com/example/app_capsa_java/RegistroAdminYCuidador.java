package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroAdminYCuidador extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_admin_y_cuidador);

        // Configuración del clic en el botón "Administrador"
        Button btnAdministrador = findViewById(R.id.btnAdministrador);
        btnAdministrador.setOnClickListener(v -> {
            // Redirigir a FormularioAdministrador
            Intent intent = new Intent(RegistroAdminYCuidador.this, FormularioAdministrador.class);
            startActivity(intent);
        });

        // Configuración del clic en el botón "Cuidador"
        Button btnCuidador = findViewById(R.id.btnCuidador);
        btnCuidador.setOnClickListener(v -> {
            // Redirigir a FormularioCuidador
            Intent intent = new Intent(RegistroAdminYCuidador.this, FormularioCuidador.class);
            startActivity(intent);
        });
    }
}
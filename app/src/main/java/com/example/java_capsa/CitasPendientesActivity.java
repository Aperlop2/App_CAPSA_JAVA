package com.example.java_capsa;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CitasPendientesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citas_pendientes);

        Button btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> finish()); // Cierra la actividad al hacer clic en "Cerrar"
    }
}

package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class EditarCuidador extends AppCompatActivity {

    private EditText etNombreEditar, etDireccionEditar, etTelefonoEditar, etHorarioEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_cuidador);

        // Referenciar vistas
        findViewById(R.id.imagenCuidador);
        etNombreEditar = findViewById(R.id.etNombreEditar);
        etDireccionEditar = findViewById(R.id.etDireccionEditar);
        etTelefonoEditar = findViewById(R.id.etTelefonoEditar);
        etHorarioEditar = findViewById(R.id.etHorarioEditar);
        Button btnGuardarEditar = findViewById(R.id.btnGuardarEditar);
        Button btnCancelarEditar = findViewById(R.id.btnCancelarEditar);

        // Obtener datos enviados desde GestionDeCuidadores
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String direccion = intent.getStringExtra("direccion");
        String telefono = intent.getStringExtra("telefono");
        String horario = intent.getStringExtra("horario");

        // Rellenar los campos con los datos recibidos
        etNombreEditar.setText(nombre);
        etDireccionEditar.setText(direccion);
        etTelefonoEditar.setText(telefono);
        etHorarioEditar.setText(horario);

        // Acción para el botón Guardar
        btnGuardarEditar.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("nombre", etNombreEditar.getText().toString());
            resultIntent.putExtra("direccion", etDireccionEditar.getText().toString());
            resultIntent.putExtra("telefono", etTelefonoEditar.getText().toString());
            resultIntent.putExtra("horario", etHorarioEditar.getText().toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Acción para el botón Cancelar
        btnCancelarEditar.setOnClickListener(v -> finish());
    }
}
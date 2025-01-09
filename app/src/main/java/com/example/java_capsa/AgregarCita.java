package com.example.java_capsa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AgregarCita extends AppCompatActivity {

    private TextView tvFecha, tvHora;
    private EditText etCuidador, etUbicacion;
    private Button btnGuardarCita, btnCancelarCita;
    private ImageView iconoFecha, iconoHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_cita);

        // Inicializar vistas
        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        etCuidador = findViewById(R.id.etCuidador);
        etUbicacion = findViewById(R.id.etUbicacion);
        btnGuardarCita = findViewById(R.id.btnGuardarCita);
        btnCancelarCita = findViewById(R.id.btnCancelarCita);
        iconoFecha = findViewById(R.id.iconoFecha);
        iconoHora = findViewById(R.id.iconoHora);

        // Selector de fecha
        iconoFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String fechaSeleccionada = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                tvFecha.setText(fechaSeleccionada);
            }, year, month, day).show();
        });

        // Selector de hora
        iconoHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String horaSeleccionada = hourOfDay + ":" + (minute1 < 10 ? "0" + minute1 : minute1);
                tvHora.setText(horaSeleccionada);
            }, hour, minute, true).show();
        });

        // Guardar cita
        btnGuardarCita.setOnClickListener(v -> {
            try {
                // Obtener los datos ingresados
                String fecha = tvFecha.getText().toString();
                String hora = tvHora.getText().toString();
                String cuidador = etCuidador.getText().toString();
                String ubicacion = etUbicacion.getText().toString();

                // Validar que los campos no estén vacíos
                if (fecha.isEmpty() || hora.isEmpty() || cuidador.isEmpty() || ubicacion.isEmpty()) {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Preparar los datos para enviarlos a la actividad principal
                Intent resultIntent = new Intent();
                resultIntent.putExtra("fecha", fecha);
                resultIntent.putExtra("hora", hora);
                resultIntent.putExtra("cuidador", cuidador);
                resultIntent.putExtra("ubicacion", ubicacion);

                // Enviar los datos a la actividad principal y cerrar la actividad
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (Exception e) {
                // Manejar errores inesperados
                Toast.makeText(this, "Error al guardar la cita: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        // Cancelar acción
        btnCancelarCita.setOnClickListener(v -> {
            // Cerrar la actividad sin guardar datos
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
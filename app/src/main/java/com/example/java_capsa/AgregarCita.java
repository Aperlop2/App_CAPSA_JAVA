package com.example.java_capsa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_cita);

        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        etCuidador = findViewById(R.id.etCuidador);
        etUbicacion = findViewById(R.id.etUbicacion);
        Button btnGuardarCita = findViewById(R.id.btnGuardarCita);
        Button btnCancelarCita = findViewById(R.id.btnCancelarCita);
        ImageView iconoFecha = findViewById(R.id.iconoFecha);
        ImageView iconoHora = findViewById(R.id.iconoHora);

        // Selección de fecha
        iconoFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String fecha = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                tvFecha.setText(fecha);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Selección de hora
        iconoHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String hora = hourOfDay + ":" + (minute1 < 10 ? "0" + minute1 : minute1);
                tvHora.setText(hora);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        // Acción al guardar
        btnGuardarCita.setOnClickListener(v -> {
            String fecha = tvFecha.getText().toString();
            String hora = tvHora.getText().toString();
            String cuidador = etCuidador.getText().toString();
            String ubicacion = etUbicacion.getText().toString();

            if (fecha.isEmpty() || hora.isEmpty() || cuidador.isEmpty() || ubicacion.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar la cita (puedes añadir lógica para enviarla a la lista principal)
            Toast.makeText(this, "Cita guardada exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Acción al cancelar
        btnCancelarCita.setOnClickListener(v -> finish());
    }
}
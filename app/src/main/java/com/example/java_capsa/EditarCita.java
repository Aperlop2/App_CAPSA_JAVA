package com.example.java_capsa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditarCita extends AppCompatActivity {

    private TextView tvFechaEditar, tvHoraEditar;
    private EditText etCuidadorEditar, etUbicacionEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_cita);

        tvFechaEditar = findViewById(R.id.tvFechaEditar);
        tvHoraEditar = findViewById(R.id.tvHoraEditar);
        etCuidadorEditar = findViewById(R.id.etCuidadorEditar);
        etUbicacionEditar = findViewById(R.id.etUbicacionEditar);
        Button btnGuardarCitaEditar = findViewById(R.id.btnGuardarCitaEditar);
        Button btnCancelarCitaEditar = findViewById(R.id.btnCancelarCitaEditar);
        ImageView iconoFechaEditar = findViewById(R.id.iconoFechaEditar);
        ImageView iconoHoraEditar = findViewById(R.id.iconoHoraEditar);

        // Recibir datos de la cita seleccionada
        Intent intent = getIntent();
        String fecha = intent.getStringExtra("fecha");
        String hora = intent.getStringExtra("hora");
        String cuidador = intent.getStringExtra("cuidador");
        String ubicacion = intent.getStringExtra("ubicacion");

        tvFechaEditar.setText(fecha);
        tvHoraEditar.setText(hora);
        etCuidadorEditar.setText(cuidador);
        etUbicacionEditar.setText(ubicacion);

        // Selector de fecha
        iconoFechaEditar.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String nuevaFecha = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                tvFechaEditar.setText(nuevaFecha);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Selector de hora
        iconoHoraEditar.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String nuevaHora = hourOfDay + ":" + (minute1 < 10 ? "0" + minute1 : minute1);
                tvHoraEditar.setText(nuevaHora);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        // Acción para guardar
        btnGuardarCitaEditar.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("fecha", tvFechaEditar.getText().toString());
            resultIntent.putExtra("hora", tvHoraEditar.getText().toString());
            resultIntent.putExtra("cuidador", etCuidadorEditar.getText().toString());
            resultIntent.putExtra("ubicacion", etUbicacionEditar.getText().toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Acción para cancelar
        btnCancelarCitaEditar.setOnClickListener(v -> finish());
    }
}
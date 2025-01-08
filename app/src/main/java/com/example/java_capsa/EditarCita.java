package com.example.java_capsa;

import android.annotation.SuppressLint;
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

public class EditarCita extends AppCompatActivity {

    private TextView tvFechaEditar, tvHoraEditar;
    private EditText etCuidadorEditar, etUbicacionEditar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_cita);

        // Enlazar vistas
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
        String detalle = intent.getStringExtra("detalle");
        String cuidador = intent.getStringExtra("cuidador");
        String ubicacion = intent.getStringExtra("ubicacion");

        // Dividir el detalle en fecha y hora
        if (detalle != null) {
            String[] detalleDividido = detalle.split(" ");
            if (detalleDividido.length >= 3) {
                tvFechaEditar.setText(detalleDividido[0]); // Fecha
                tvHoraEditar.setText(detalleDividido[1] + " " + detalleDividido[2]); // Hora (con AM/PM)
            } else if (detalleDividido.length == 2) {
                tvFechaEditar.setText(detalleDividido[0]); // Fecha
                tvHoraEditar.setText(detalleDividido[1]); // Hora (sin AM/PM)
            } else {
                tvFechaEditar.setText("Fecha no disponible");
                tvHoraEditar.setText("Hora no disponible");
            }
        }

        // Asignar valores a los campos restantes
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

        // Guardar cambios
        btnGuardarCitaEditar.setOnClickListener(v -> {
            if (tvFechaEditar.getText().toString().isEmpty() ||
                    tvHoraEditar.getText().toString().isEmpty() ||
                    etCuidadorEditar.getText().toString().isEmpty() ||
                    etUbicacionEditar.getText().toString().isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            String nuevoDetalle = tvFechaEditar.getText().toString() + " " + tvHoraEditar.getText().toString();
            resultIntent.putExtra("detalle", nuevoDetalle);
            resultIntent.putExtra("cuidador", etCuidadorEditar.getText().toString());
            resultIntent.putExtra("ubicacion", etUbicacionEditar.getText().toString());
            resultIntent.putExtra("position", intent.getIntExtra("position", -1));
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Cancelar edición
        btnCancelarCitaEditar.setOnClickListener(v -> finish());
    }
}
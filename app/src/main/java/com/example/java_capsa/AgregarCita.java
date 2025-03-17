package com.example.java_capsa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AgregarCita extends AppCompatActivity {

    private static final String TAG = "AgregarCita";
    private TextView tvFecha, tvHora, tvDatoDinamico;
    private EditText etUbicacion;
    private DatabaseReference firebaseReference;
    private String correoCuidador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_cita);

        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        tvDatoDinamico = findViewById(R.id.tvDatoDinamico);
        etUbicacion = findViewById(R.id.etUbicacion);
        Button btnGuardarCita = findViewById(R.id.btnGuardarCita);
        Button btnCancelarCita = findViewById(R.id.btnCancelarCita);
        ImageView iconoFecha = findViewById(R.id.iconoFecha);
        ImageView iconoHora = findViewById(R.id.iconoHora);

        firebaseReference = FirebaseDatabase.getInstance().getReference("citas");

        // Obtener el correo del cuidador desde la actividad anterior
        correoCuidador = getIntent().getStringExtra("correo_cuidador");

        if (correoCuidador == null || correoCuidador.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo identificar el correo del cuidador", Toast.LENGTH_SHORT).show();
            tvDatoDinamico.setText("Correo no identificado");
            finish();
            return;
        } else {
            // Mostrar el correo del cuidador en el TextView
            tvDatoDinamico.setText(correoCuidador);
        }

        iconoFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String fechaSeleccionada = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                tvFecha.setText(fechaSeleccionada);
            }, year, month, day).show();
        });

        iconoHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String horaSeleccionada = hourOfDay + ":" + (minute1 < 10 ? "0" + minute1 : minute1) + ":00";
                tvHora.setText(horaSeleccionada);
            }, hour, minute, true).show();
        });

        btnGuardarCita.setOnClickListener(v -> {
            try {
                String fecha = tvFecha.getText().toString();
                String hora = tvHora.getText().toString();
                String ubicacion = etUbicacion.getText().toString();

                if (fecha.isEmpty() || hora.isEmpty() || ubicacion.isEmpty() || correoCuidador == null) {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                guardarCitaEnFirebase(fecha, hora, ubicacion, correoCuidador);
                guardarCitaEnMySQL(fecha, hora, ubicacion, correoCuidador);

                Toast.makeText(this, "Cita guardada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar la cita: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: ", e);
            }
        });

        btnCancelarCita.setOnClickListener(v -> finish());
    }

    private void guardarCitaEnFirebase(String fecha, String hora, String ubicacion, String correoCuidador) {
        String idCita = firebaseReference.push().getKey();
        if (idCita != null) {
            Map<String, Object> cita = new HashMap<>();
            cita.put("fecha", fecha);
            cita.put("hora", hora);
            cita.put("ubicacion", ubicacion);
            cita.put("correo_cuidador", correoCuidador);
            firebaseReference.child(idCita).setValue(cita)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Cita guardada en Firebase"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al guardar en Firebase: ", e));
        }
    }

    private void guardarCitaEnMySQL(String fecha, String hora, String ubicacion, String correoCuidador) {
        String url = "http://192.168.137.1/evidencias/guardar_cita.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Log.d(TAG, "Cita guardada en SQLite: " + response),
                error -> Log.e(TAG, "Error al guardar en SQLite: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fecha", fecha);
                params.put("hora", hora);
                params.put("ubicacion", ubicacion);
                params.put("correo_cuidador", correoCuidador);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}

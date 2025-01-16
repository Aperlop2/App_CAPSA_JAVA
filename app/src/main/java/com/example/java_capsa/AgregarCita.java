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
import com.example.myapplication.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_cita);

        // Inicializar vistas
        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        tvDatoDinamico = findViewById(R.id.tvDatoDinamico);
        etUbicacion = findViewById(R.id.etUbicacion);
        Button btnGuardarCita = findViewById(R.id.btnGuardarCita);
        Button btnCancelarCita = findViewById(R.id.btnCancelarCita);
        ImageView iconoFecha = findViewById(R.id.iconoFecha);
        ImageView iconoHora = findViewById(R.id.iconoHora);

        firebaseReference = FirebaseDatabase.getInstance().getReference("citas");

        // Obtener el cuidador enviado desde la actividad anterior
        String cuidadorSeleccionado = getIntent().getStringExtra("cuidador");

        // Mostrar el cuidador en el TextView dinámico
        if (cuidadorSeleccionado != null && !cuidadorSeleccionado.isEmpty()) {
            tvDatoDinamico.setText("Cita con: " + cuidadorSeleccionado);
        } else {
            tvDatoDinamico.setText("Cita sin cuidador seleccionado");
        }

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
                String ubicacion = etUbicacion.getText().toString();

                // Validar que los campos no estén vacíos
                if (fecha.isEmpty() || hora.isEmpty() || ubicacion.isEmpty()) {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Almacenar en Firebase
                guardarCitaEnFirebase(cuidadorSeleccionado, fecha, hora, ubicacion);

                // Almacenar en MySQL
                guardarCitaEnMySQL(cuidadorSeleccionado, fecha, hora, ubicacion);

                // Finalizar actividad
                Toast.makeText(this, "Cita guardada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar la cita: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: ", e);
            }
        });

        // Cancelar acción
        btnCancelarCita.setOnClickListener(v -> finish());
    }

    private void guardarCitaEnFirebase(String cuidador, String fecha, String hora, String ubicacion) {
        String idCita = firebaseReference.push().getKey();
        if (idCita != null) {
            Map<String, Object> cita = new HashMap<>();
            cita.put("cuidador", cuidador);
            cita.put("fecha", fecha);
            cita.put("hora", hora);
            cita.put("ubicacion", ubicacion);
            firebaseReference.child(idCita).setValue(cita)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Cita guardada en Firebase"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al guardar en Firebase: ", e));
        }
    }

    private void guardarCitaEnMySQL(String cuidador, String fecha, String hora, String ubicacion) {
        String url = "http://192.168.137.1/evidencias/guardar_cita.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Log.d(TAG, "Cita guardada en MySQL: " + response),
                error -> Log.e(TAG, "Error al guardar en MySQL: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cuidador", cuidador);
                params.put("fecha", fecha);
                params.put("hora", hora);
                params.put("ubicacion", ubicacion);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
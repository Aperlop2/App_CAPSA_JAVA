package com.example.java_capsa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentsActivity";
    private CitaAdapter adapter;
    private List<Cita> citasList;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private String correoCuidadorActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointments);

        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        citasList = new ArrayList<>();
        adapter = new CitaAdapter(citasList, this);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("citas");

        obtenerCorreoDelCuidador();
    }

    private void obtenerCorreoDelCuidador() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        correoCuidadorActual = sharedPreferences.getString("correo_cuidador", null);

        if (correoCuidadorActual != null) {
            Log.d(TAG, "✅ Correo del cuidador logueado: " + correoCuidadorActual);
            cargarCitasDelCuidador();
        } else {
            Log.e(TAG, "❌ No se encontró el correo del cuidador en SharedPreferences.");
            Toast.makeText(this, "Error al obtener datos del usuario.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarCitasDelCuidador() {
        Log.d(TAG, "🔍 Cargando citas solo del cuidador: " + correoCuidadorActual);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "📌 Total de citas en la base de datos: " + snapshot.getChildrenCount());

                citasList.clear();
                boolean hayCitas = false;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String idCita = ds.getKey();
                    String correoCita = ds.child("correo_cuidador").getValue(String.class);
                    String fecha = ds.child("fecha").getValue(String.class);
                    String hora = ds.child("hora").getValue(String.class);
                    String ubicacion = ds.child("ubicacion").getValue(String.class);
                    String estado = ds.child("estado").getValue(String.class);
                    if (estado == null) estado = "Pendiente";

                    if (correoCita != null && correoCita.equals(correoCuidadorActual)) {
                        if (fecha != null && hora != null && ubicacion != null) {
                            Cita nuevaCita = new Cita(idCita, fecha, hora, ubicacion, estado);
                            citasList.add(nuevaCita);
                            Log.d(TAG, "✅ Cita añadida: " + nuevaCita.getFecha() + ", " + nuevaCita.getHora() + ", " + nuevaCita.getUbicacion());
                            hayCitas = true;
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                if (!hayCitas) {
                    Log.e(TAG, "❌ No hay citas asignadas a este cuidador.");
                    Toast.makeText(AppointmentsActivity.this, "No tienes citas asignadas.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppointmentsActivity.this, "Citas cargadas correctamente.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "❌ ERROR al cargar citas desde Firebase: " + error.getMessage());
            }
        });
    }
}

package com.example.java_capsa;

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

        cargarTodasLasCitasDesdeFirebase(); // Ahora carga TODAS las citas
    }

    private void cargarTodasLasCitasDesdeFirebase() {
        Log.d(TAG, "🔍 Cargando TODAS las citas desde Firebase...");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "📌 Total de citas en la base de datos: " + snapshot.getChildrenCount());

                citasList.clear();
                boolean hayCitas = false;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String fecha = ds.child("fecha").getValue(String.class);
                    String hora = ds.child("hora").getValue(String.class);
                    String ubicacion = ds.child("ubicacion").getValue(String.class);
                    String correoCuidador = ds.child("correo_cuidador").getValue(String.class);

                    if (fecha != null && hora != null && ubicacion != null && correoCuidador != null) {
                        Cita nuevaCita = new Cita(fecha, hora, ubicacion);
                        citasList.add(nuevaCita);
                        Log.d(TAG, "✅ Cita añadida: " + nuevaCita.getFecha() + ", " + nuevaCita.getHora() + ", " + nuevaCita.getUbicacion() + " - Cuidador: " + correoCuidador);
                        hayCitas = true;
                    }
                }

                adapter.notifyDataSetChanged();

                if (!hayCitas) {
                    Log.e(TAG, "❌ No hay citas registradas en Firebase.");
                    Toast.makeText(AppointmentsActivity.this, "No hay citas registradas.", Toast.LENGTH_SHORT).show();
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

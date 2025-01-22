package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiciosCompletadosActivity extends AppCompatActivity {

    private static final String TAG = "ServiciosCompletados";
    private RecyclerView recyclerView;
    private CitaAdapter adapter;
    private List<Cita> citasList;
    private DatabaseReference databaseReference;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios_completados);

        recyclerView = findViewById(R.id.scroll_servicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        citasList = new ArrayList<>();
        adapter = new CitaAdapter(citasList, this);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("citas");

        cargarCitasCompletadas();
    }

    private void cargarCitasCompletadas() {
        databaseReference.orderByChild("estado").equalTo("Completada")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        citasList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String idCita = ds.getKey(); // Obtener el ID de la cita
                            String fecha = ds.child("fecha").getValue(String.class);
                            String hora = ds.child("hora").getValue(String.class);
                            String ubicacion = ds.child("ubicacion").getValue(String.class);
                            String correoCuidador = ds.child("correo_cuidador").getValue(String.class);
                            String estado = ds.child("estado").getValue(String.class);

                            if (estado == null) estado = "Pendiente"; // Si no tiene estado, asignamos "Pendiente"

                            if (fecha != null && hora != null && ubicacion != null && correoCuidador != null) {
                                Cita nuevaCita = new Cita(idCita, fecha, hora, ubicacion, estado);
                                citasList.add(nuevaCita);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        Toast.makeText(ServiciosCompletadosActivity.this, "Servicios completados cargados", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "❌ ERROR al cargar citas completadas: " + error.getMessage());
                    }
                });
    }
}

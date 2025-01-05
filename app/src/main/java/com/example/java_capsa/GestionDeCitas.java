package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GestionDeCitas extends AppCompatActivity {

    private CitaAdapter adapter;
    private List<Cita> citasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_de_citas);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCitas);
        Button btnAgregarCita = findViewById(R.id.btnAgregarCita);

        // Configuración del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        citasList = new ArrayList<>();
        adapter = new CitaAdapter(citasList);
        recyclerView.setAdapter(adapter);

        // Botón para agregar nueva cita
        btnAgregarCita.setOnClickListener(v -> {
            // Lógica para abrir pantalla de agregar citas
        });

        // Cargar citas de ejemplo
        cargarCitas();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarCitas() {
        // Ejemplo de citas cargadas
        citasList.add(new Cita("Cita 1: 01/01/2024 10:00 AM", "Cuidadora: María Roldán González", "Ubicación: Calle x, #123"));
        citasList.add(new Cita("Cita 2: 01/01/2024 11:00 AM", "Cuidador: Marcos Jiménez Aguilar", "Ubicación: Calle x, #124"));
        citasList.add(new Cita("Cita 3: 01/01/2024 12:00 PM", "Cuidadora: José Martínez Esquivel", "Ubicación: Calle x, #125"));
        adapter.notifyDataSetChanged();
    }
}
package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class HistorialEvidencias extends AppCompatActivity {

    private EvidenciaAdapter adapter;
    private List<Evidencia> evidenciasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_evidencias);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvidencias);

        // Configuración del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        evidenciasList = new ArrayList<>();
        adapter = new EvidenciaAdapter(evidenciasList);
        recyclerView.setAdapter(adapter);

        // Cargar datos de ejemplo
        cargarEvidencias();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarEvidencias() {
        evidenciasList.add(new Evidencia("Fecha: 01/01/2024 Hora: 10:00 AM\nUbicación: Calle x, #123", 3, "¡Bien!", R.drawable.avatar));
        evidenciasList.add(new Evidencia("Fecha: 01/01/2024 Hora: 11:00 AM\nUbicación: Calle y, #456", 4, "¡Muy bien!", R.drawable.avatar));
        evidenciasList.add(new Evidencia("Fecha: 01/01/2024 Hora: 12:00 PM\nUbicación: Calle z, #789", 5, "¡Excelente felicidades!", R.drawable.avatar));
        adapter.notifyDataSetChanged();
    }
}
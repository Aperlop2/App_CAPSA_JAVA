package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

        // Inicializar vistas
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCita);
        Button btnAgregarCita = findViewById(R.id.btnAgregarCita);

        // Configuración del RecyclerView
        citasList = new ArrayList<>();
        adapter = new CitaAdapter(citasList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Acción del botón "Agregar"
        btnAgregarCita.setOnClickListener(v -> {
            Intent intent = new Intent(GestionDeCitas.this, AgregarCita.class);
            startActivityForResult(intent, 100); // Código único para identificar la acción
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Agregar nueva cita desde AgregarCita
            String fecha = data.getStringExtra("fecha");
            String hora = data.getStringExtra("hora");
            String cuidador = data.getStringExtra("cuidador");
            String ubicacion = data.getStringExtra("ubicacion");

            if (fecha != null && hora != null && cuidador != null && ubicacion != null) {
                // Crear el detalle con formato correcto
                String detalle = fecha + " " + hora;
                citasList.add(new Cita(detalle, cuidador, ubicacion));
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Cita agregada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: Datos incompletos al agregar cita", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            // Editar cita desde EditarCita
            int position = data.getIntExtra("position", -1);
            String detalle = data.getStringExtra("detalle");
            String cuidador = data.getStringExtra("cuidador");
            String ubicacion = data.getStringExtra("ubicacion");

            if (position >= 0 && detalle != null && cuidador != null && ubicacion != null) {
                // Actualizar cita existente
                Cita citaEditada = citasList.get(position);
                citaEditada.setDetalle(detalle);
                citaEditada.setCuidador(cuidador);
                citaEditada.setUbicacion(ubicacion);
                adapter.notifyItemChanged(position);
                Toast.makeText(this, "Cita actualizada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: Datos incompletos al editar cita", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Acción cancelada", Toast.LENGTH_SHORT).show();
        }
    }
}
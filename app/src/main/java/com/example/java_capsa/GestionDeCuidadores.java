package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GestionDeCuidadores extends AppCompatActivity {

    private CuidadorAdapter adapter;
    private List<Cuidador> cuidadoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_de_cuidadores);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCuidadores);
        Button botonAgregar = findViewById(R.id.botonAgregar);

        // Configuración del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cuidadoresList = new ArrayList<>();
        adapter = new CuidadorAdapter(cuidadoresList);
        recyclerView.setAdapter(adapter);

        // Botón para agregar nuevo cuidador
        botonAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarNuevoCuidador.class);
            startActivity(intent);
        });

        // Obtener datos del nuevo cuidador
        recibirNuevoCuidador();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void recibirNuevoCuidador() {
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String direccion = intent.getStringExtra("direccion");
        String telefono = intent.getStringExtra("telefono");
        String horario = intent.getStringExtra("horario");
        String imageUri = intent.getStringExtra("imageUri");

        if (nombre != null && direccion != null && telefono != null && horario != null && imageUri != null) {
            Cuidador nuevoCuidador = new Cuidador(nombre, direccion, telefono, horario, imageUri);
            cuidadoresList.add(nuevoCuidador);
            adapter.notifyDataSetChanged();
        }
    }
}
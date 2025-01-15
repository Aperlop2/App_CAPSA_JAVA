package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GestionDeCuidadoresActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<String> caregivers; // Lista de nombres de cuidadores
    private List<String> caregiverIds; // Lista de IDs únicos de cuidadores

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_de_cuidadores);

        // Vincular el ListView
        ListView caregiverListView = findViewById(R.id.caregiverListView);

        // Inicializar listas
        caregivers = new ArrayList<>();
        caregiverIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, caregivers);
        caregiverListView.setAdapter(adapter);

        // Referencia a la base de datos Firebase
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("solicitudes_cuidadores");

        // Escuchar cambios en la base de datos
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                caregivers.clear(); // Limpiar lista de nombres
                caregiverIds.clear(); // Limpiar lista de IDs

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String caregiverId = snapshot.getKey(); // ID único del cuidador
                    String caregiverName = snapshot.child("nombre").getValue(String.class);
                    String status = snapshot.child("estado").getValue(String.class);

                    // Verificar que el cuidador tenga estado "pendiente"
                    if (caregiverName != null && "pendiente".equals(status)) {
                        caregivers.add(caregiverName); // Agregar nombre a la lista
                        caregiverIds.add(caregiverId); // Agregar ID único a la lista
                    }
                }

                // Actualizar el adaptador
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GestionDeCuidadoresActivity.this, "Error al cargar datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });

        // Configurar clic en un elemento del ListView
        caregiverListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCaregiverId = caregiverIds.get(position); // Obtener el ID del cuidador seleccionado
            Intent intent = new Intent(GestionDeCuidadoresActivity.this, AgregarNuevoCuidadorActivity.class);
            intent.putExtra("caregiver_id", selectedCaregiverId); // Pasar el ID del cuidador a la siguiente actividad
            startActivity(intent);
        });
    }
}
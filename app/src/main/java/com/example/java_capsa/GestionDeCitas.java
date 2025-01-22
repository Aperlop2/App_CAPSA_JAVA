package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionDeCitas extends AppCompatActivity {

    private static final String TAG = "GestionDeCitas";
    private ArrayAdapter<String> adapter;
    private List<String> cuidadoresNombresList;
    private Map<String, String> cuidadoresMap; // Map para almacenar nombre -> correo
    private DatabaseReference firebaseReference;
    private ListView listViewCuidadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.gestion_de_citas);
        } catch (Exception e) {
            Log.e(TAG, "Error al inflar el layout: " + e.getMessage());
            Toast.makeText(this, "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar componentes
        try {
            listViewCuidadores = findViewById(R.id.listViewCuidadores);

            if (listViewCuidadores == null) {
                Log.e(TAG, "Error: El ListView no existe en el layout.");
                Toast.makeText(this, "Error al cargar la interfaz.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            cuidadoresNombresList = new ArrayList<>();
            cuidadoresMap = new HashMap<>();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cuidadoresNombresList) {
                @Override
                public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                    android.view.View view = super.getView(position, convertView, parent);
                    TextView textView = view.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(android.R.color.black)); // Aplicar color negro
                    return view;
                }
            };
            listViewCuidadores.setAdapter(adapter);

            // Referencia a la base de datos Firebase
            firebaseReference = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

            // Cargar cuidadores autenticados
            cargarCuidadoresDesdeFirebase();

            // Configurar clic en los elementos del ListView
            listViewCuidadores.setOnItemClickListener((parent, view, position, id) -> {
                String nombreSeleccionado = cuidadoresNombresList.get(position);
                String correoCuidador = cuidadoresMap.get(nombreSeleccionado);

                if (correoCuidador != null) {
                    // Redirigir a AgregarCita enviando el correo en lugar del nombre
                    Intent intent = new Intent(GestionDeCitas.this, AgregarCita.class);
                    intent.putExtra("correo_cuidador", correoCuidador);
                    startActivity(intent);
                } else {
                    Toast.makeText(GestionDeCitas.this, "Error: No se pudo obtener el correo del cuidador", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Correo no encontrado para el cuidador: " + nombreSeleccionado);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error durante la inicialización: " + e.getMessage());
            Toast.makeText(this, "Error en la inicialización de la actividad.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarCuidadoresDesdeFirebase() {
        firebaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cuidadoresNombresList.clear();
                cuidadoresMap.clear();

                for (DataSnapshot cuidadorSnapshot : snapshot.getChildren()) {
                    String nombre = cuidadorSnapshot.child("nombre").getValue(String.class);
                    String correo = cuidadorSnapshot.child("correo").getValue(String.class);

                    if (nombre != null && correo != null) {
                        cuidadoresNombresList.add(nombre);
                        cuidadoresMap.put(nombre, correo);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al cargar cuidadores desde Firebase: " + error.getMessage());
                Toast.makeText(GestionDeCitas.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GestionDeCitas extends AppCompatActivity {

    private static final String TAG = "GestionDeCitas";
    private ArrayAdapter<String> adapter;
    private List<String> cuidadoresList;
    private DatabaseReference firebaseReference;

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
            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            ListView listViewCuidadores = findViewById(R.id.listViewCuidadores);

            if (listViewCuidadores == null) {
                Log.e(TAG, "Error: El ListView no existe en el layout.");
                Toast.makeText(this, "Error al cargar la interfaz.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            cuidadoresList = new ArrayList<>();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cuidadoresList);
            listViewCuidadores.setAdapter(adapter);

            firebaseReference = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

            // Cargar cuidadores autenticados y registrados
            cargarCuidadores();

            // Configurar clic en los elementos del ListView
            listViewCuidadores.setOnItemClickListener((parent, view, position, id) -> {
                String cuidadorSeleccionado = cuidadoresList.get(position);

                // Redirigir a la actividad AgregarCita con el cuidador seleccionado
                Intent intent = new Intent(GestionDeCitas.this, AgregarCita.class);
                intent.putExtra("cuidador", cuidadorSeleccionado);
                startActivity(intent);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error durante la inicialización: " + e.getMessage());
            Toast.makeText(this, "Error en la inicialización de la actividad.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarCuidadores() {
        Set<String> firebaseCorreos = new HashSet<>();

        // Obtener cuidadores desde Firebase
        firebaseReference.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String correo = childSnapshot.child("correo").getValue(String.class);
                    if (correo != null) {
                        firebaseCorreos.add(correo.trim());
                        Log.d(TAG, "Correo desde Firebase: " + correo);
                    }
                }
            } else {
                Log.w(TAG, "No se encontraron cuidadores en Firebase.");
            }

            // Filtrar desde MySQL los que coincidan
            cargarCuidadoresDesdeMySQL(firebaseCorreos);
        }).addOnFailureListener(error -> {
            Log.e(TAG, "Error al cargar cuidadores desde Firebase: " + error.getMessage());
            Toast.makeText(this, "Error al cargar cuidadores desde Firebase", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarCuidadoresDesdeMySQL(Set<String> firebaseCorreos) {
        String url = "http://192.168.137.1/evidencias/obtener_cuidadores.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        if (!response.isEmpty()) {
                            String[] mysqlCorreosArray = response.split(",");
                            Set<String> mysqlCorreos = new HashSet<>();
                            for (String correo : mysqlCorreosArray) {
                                mysqlCorreos.add(correo.trim());
                                Log.d(TAG, "Correo desde MySQL: " + correo.trim());
                            }

                            // Filtrar cuidadores autenticados
                            firebaseCorreos.retainAll(mysqlCorreos);
                            Log.d(TAG, "Cuidadores autenticados y registrados: " + firebaseCorreos);

                            actualizarLista(firebaseCorreos);
                        } else {
                            Log.w(TAG, "Respuesta vacía desde MySQL.");
                            Toast.makeText(this, "No se encontraron cuidadores en MySQL.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar datos de MySQL: " + e.getMessage());
                        Toast.makeText(this, "Error al procesar datos de MySQL.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error al cargar datos desde MySQL: " + error.getMessage());
                    Toast.makeText(this, "Error al cargar datos desde MySQL.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void actualizarLista(Set<String> cuidadores) {
        if (cuidadores.isEmpty()) {
            Toast.makeText(this, "No hay cuidadores registrados para mostrar.", Toast.LENGTH_SHORT).show();
        } else {
            cuidadoresList.clear();
            cuidadoresList.addAll(cuidadores);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Lista de cuidadores actualizada con éxito.");
        }
    }
}

package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class AppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentsActivity";
    private CitaAdapter adapter;
    private List<Cita> citasList;
    private Set<String> citasUnicas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointments);

        // Inicializar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        citasList = new ArrayList<>();
        citasUnicas = new HashSet<>();
        adapter = new CitaAdapter(citasList, this, null); // Sin listener, solo mostrar citas
        recyclerView.setAdapter(adapter);

        // Cargar citas del cuidador autenticado
        cargarCitas();
    }

    private void cargarCitas() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String cuidador = user.getEmail(); // Correo del cuidador autenticado

        // Cargar citas desde Firebase
        cargarCitasDesdeFirebase(cuidador);

        // Cargar citas desde MySQL
        cargarCitasDesdeMySQL(cuidador);
    }

    private void cargarCitasDesdeFirebase(String cuidador) {
        FirebaseDatabase.getInstance().getReference("citas")
                .orderByChild("cuidador").equalTo(cuidador)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String fecha = ds.child("fecha").getValue(String.class);
                            String hora = ds.child("hora").getValue(String.class);
                            String ubicacion = ds.child("ubicacion").getValue(String.class);

                            // Crear un identificador único para la cita
                            String citaId = fecha + " " + hora + " " + ubicacion;

                            // Agregar solo si no existe en el conjunto
                            if (citasUnicas.add(citaId)) {
                                citasList.add(new Cita(fecha + " " + hora, cuidador, ubicacion));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error al cargar citas desde Firebase: " + error.getMessage());
                        Toast.makeText(AppointmentsActivity.this, "Error al cargar citas desde Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarCitasDesdeMySQL(String cuidador) {
        String url = "http://192.168.137.1/evidencias/obtener_citas_cuidador.php";

        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String fecha = jsonObject.getString("fecha");
                            String hora = jsonObject.getString("hora");
                            String ubicacion = jsonObject.getString("ubicacion");

                            // Crear un identificador único para la cita
                            String citaId = fecha + " " + hora + " " + ubicacion;

                            // Agregar solo si no existe en el conjunto
                            if (citasUnicas.add(citaId)) {
                                citasList.add(new Cita(fecha + " " + hora, cuidador, ubicacion));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error al procesar datos de MySQL: " + e.getMessage());
                        Toast.makeText(this, "Error al procesar datos de MySQL", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error al cargar citas desde MySQL: " + error.getMessage());
                    Toast.makeText(this, "Error al cargar citas desde MySQL", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cuidador", cuidador);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
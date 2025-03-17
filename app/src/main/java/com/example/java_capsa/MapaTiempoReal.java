package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MapaTiempoReal extends AppCompatActivity {
    private CuidadorAdapter adapter;
    private final List<Cuidador> listaCuidadores = new ArrayList<>();
    private static final String URL = "http://192.168.100.5/obtener_evidencias.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_tiempo_real);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCuidadores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CuidadorAdapter(listaCuidadores);
        recyclerView.setAdapter(adapter);

        obtenerCuidadores();
    }

    private void obtenerCuidadores() {
        RequestQueue queue = Volley.newRequestQueue(this);

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        Log.e("RESPUESTA_SERVIDOR", response.toString());

                        if (response.getString("status").equals("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            listaCuidadores.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);

                                int id = obj.getInt("id");
                                String nombre = obj.getString("nombre_cuidador");
                                String ubicacion = obj.getString("ubicacion");
                                String descripcion = obj.optString("descripcion", "Sin descripción");
                                String foto = obj.optString("foto", "URL_DEFAULT");
                                String fechaHora = obj.getString("fecha_hora");

                                listaCuidadores.add(new Cuidador(id, nombre, ubicacion, descripcion, foto, fechaHora));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("Volley", "Error en respuesta: " + response.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("Volley", "Error procesando JSON: " + e.getMessage());
                    }
                },
                error -> Log.e("Volley", "Error en la solicitud: " + error.getMessage())
        );

        queue.add(jsonObjectRequest);
    }
}

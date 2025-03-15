package com.example.java_capsa;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapaTiempoReal extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CuidadorAdapter adapter;
    private List<Cuidador> listaCuidadores = new ArrayList<>();
    private static final String URL = "http://192.168.100.17/obtener_evidencias.php"; // Ajusta la IP según tu servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_tiempo_real);

        recyclerView = findViewById(R.id.recyclerViewCuidadores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CuidadorAdapter(this, listaCuidadores, this::eliminarEvidencia);
        recyclerView.setAdapter(adapter);

        obtenerCuidadores();
    }

    private void obtenerCuidadores() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("RESPUESTA_SERVIDOR", response.toString()); // Debug

                            if (response.getString("status").equals("success")) {
                                JSONArray dataArray = response.getJSONArray("data");
                                listaCuidadores.clear();

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject obj = dataArray.getJSONObject(i);

                                    int id = obj.getInt("id"); // ID de la evidencia
                                    String nombre = obj.getString("nombre_cuidador");
                                    String ubicacion = obj.getString("ubicacion");
                                    String descripcion = obj.has("descripcion") ? obj.getString("descripcion") : "Sin descripción";
                                    String foto = obj.has("foto") ? obj.getString("foto") : "URL_DEFAULT";
                                    String fechaHora = obj.getString("fecha_hora");

                                    listaCuidadores.add(new Cuidador(id, nombre, ubicacion, descripcion, foto, fechaHora));
                                }

                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("Volley", "Error en respuesta: " + response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Volley", "Error procesando JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error en la solicitud: " + error.getMessage());
                    }
                }
        );

        queue.add(jsonObjectRequest);
    }

    private void eliminarEvidencia(int id, int position) {
        String url = "http://192.168.100.17/obtener_evidencias.php?id=" + id; // ID en la URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        Log.e("DELETE_RESPONSE", response.toString()); // Mostrar respuesta del servidor en logs
                        if (response.getString("status").equals("success")) {
                            // Eliminar también de la base de datos local SQLite
                            DatabaseHelper dbHelper = new DatabaseHelper(MapaTiempoReal.this);
                            boolean eliminadoLocal = dbHelper.eliminarEvidencia(id);

                            if (eliminadoLocal) {
                                Log.e("SQLite", "Evidencia eliminada de la base de datos local.");
                            } else {
                                Log.e("SQLite", "No se encontró la evidencia en SQLite.");
                            }

                            listaCuidadores.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, listaCuidadores.size());
                        } else {
                            Log.e("DELETE_ERROR", "Error en respuesta: " + response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("DELETE_ERROR", "Error al eliminar: " + error.getMessage())
        );

        requestQueue.add(request);
    }


}

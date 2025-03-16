package com.example.java_capsa;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ListaCuidadoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CuidadorpendienteAdapter adapter;
    private List<Cuidadorpendiente> listaCuidadores;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_cuidadores);

        recyclerView = findViewById(R.id.recycler_cuidadores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaCuidadores = new ArrayList<>();
        adapter = new CuidadorpendienteAdapter(listaCuidadores, new CuidadorpendienteAdapter.OnItemClickListener() {
            @Override
            public void onAceptarClick(Cuidadorpendiente cuidador) {
                Toast.makeText(ListaCuidadoresActivity.this, "Cuidado aceptado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRechazarClick(Cuidadorpendiente cuidador) {
                Toast.makeText(ListaCuidadoresActivity.this, "Cuidado rechazado", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        // Obtener datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCuidadores.clear();
                for (DataSnapshot cuidadorSnapshot : snapshot.getChildren()) {
                    Cuidadorpendiente cuidador = cuidadorSnapshot.getValue(Cuidadorpendiente.class);
                    if (cuidador != null) {
                        listaCuidadores.add(cuidador);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error al obtener datos", error.toException());
            }
        });
    }
}

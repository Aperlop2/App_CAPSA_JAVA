package com.example.java_capsa;

import android.annotation.SuppressLint;
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
    private List<String> caregivers;
    private List<String> caregiverIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_de_cuidadores);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ListView caregiverListView = findViewById(R.id.ccaregiverListView);

        caregivers = new ArrayList<>();
        caregiverIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, caregivers);
        caregiverListView.setAdapter(adapter);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("solicitudes_cuidadores");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                caregivers.clear();
                caregiverIds.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String caregiverId = snapshot.getKey();
                    String caregiverName = snapshot.child("nombre").getValue(String.class);
                    String status = snapshot.child("estado").getValue(String.class);

                    if (caregiverName != null && "pendiente".equals(status)) {
                        caregivers.add(caregiverName);
                        caregiverIds.add(caregiverId);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GestionDeCuidadoresActivity.this, "Error al cargar datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        caregiverListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCaregiverId = caregiverIds.get(position);
            Intent intent = new Intent(GestionDeCuidadoresActivity.this, AgregarNuevoCuidadorActivity.class);
            intent.putExtra("caregiver_id", selectedCaregiverId);
            startActivity(intent);
        });
    }
}

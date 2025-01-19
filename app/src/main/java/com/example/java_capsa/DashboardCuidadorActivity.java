package com.example.java_capsa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardCuidadorActivity extends AppCompatActivity {

    private TextView tvClientName, tvAppointmentTime;
    private ImageView ivClientAvatar;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ImageView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Referencias a los elementos en el layout
        tvClientName = findViewById(R.id.clientName);
        tvAppointmentTime = findViewById(R.id.appointmentTime);
        ivClientAvatar = findViewById(R.id.clientAvatar);
        btnLogout = findViewById(R.id.notificationsIcon);
        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

        // Obtener los datos del cuidador actual
        obtenerDatosCuidador();

        btnLogout.setOnClickListener(view -> cerrarSesion());

    }

    private void obtenerDatosCuidador() {
        String userId = auth.getCurrentUser().getUid();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtener los datos del cuidador desde Firebase
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String especialidad = snapshot.child("especialidad").getValue(String.class);
                    String fotoPerfilUrl = snapshot.child("fotoPerfil").getValue(String.class);

                    // Mostrar los datos en la interfaz
                    tvClientName.setText(nombre != null ? nombre : "Nombre no disponible");
                    tvAppointmentTime.setText(especialidad != null ? especialidad : "Especialidad no disponible");

                    // Cargar la imagen con Glide si existe una URL válida
                    if (fotoPerfilUrl != null && !fotoPerfilUrl.isEmpty()) {
                        Glide.with(DashboardCuidadorActivity.this)
                                .load(fotoPerfilUrl)
                                .placeholder(R.drawable.baseline_account_circle_24) // Imagen predeterminada si no hay foto
                                .into(ivClientAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardCuidadorActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cerrarSesion() {
        auth.signOut(); // Cierra sesión de Firebase

        // Regresa a la pantalla de inicio de sesión
        Intent intent = new Intent(DashboardCuidadorActivity.this, LoginPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Borra las actividades anteriores
        startActivity(intent);
        finish(); // Cierra esta actividad para evitar que el usuario vuelva atrás con el botón de retroceso
    }

}

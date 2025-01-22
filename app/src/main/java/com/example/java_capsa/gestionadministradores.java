package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class gestionadministradores extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_administradores);

        // Configurar la foto de perfil en lugar del botón de ajustes
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios/administradores").child(userId);

            userRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String photoUrl = snapshot.child("fotoPerfil").getValue(String.class);
                    ImageView btnSettings = findViewById(R.id.btn_settings);
                    if (photoUrl != null) {
                        Glide.with(this).load(photoUrl).circleCrop().into(btnSettings);
                    }
                }
            });
        }

        // Configuración del clic en las tarjetas del panel de administración
        findViewById(R.id.card_citas_pendientes).setOnClickListener(v -> mostrarVentanaCitasPendientes(this));
        // Redirecciones a otras actividades desde los iconos inferiores
        findViewById(R.id.icon_map).setOnClickListener(v -> startActivity(new Intent(this, MapaTiempoReal.class)));
        findViewById(R.id.icon_caregivers).setOnClickListener(v -> startActivity(new Intent(this, GestionDeCuidadoresActivity.class)));
        findViewById(R.id.icon_appointments).setOnClickListener(v -> startActivity(new Intent(this, GestionDeCitas.class)));

        // Configuración del clic en el botón "Regresar"
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            Intent intent = new Intent(gestionadministradores.this, LoginPrincipal.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual para evitar volver con el botón de retroceso
        });
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaCitasPendientes(Context context) {
        if (context == null) {
            return;
        }

        // Inflar el diseño de la ventana emergente
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.citas_pendientes, null);


        if (popupView == null) {
            return;
        }

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Referencias a los elementos de la ventana emergente
        TextView tvTotalCitas = popupView.findViewById(R.id.tv_total_citas);
        LinearLayout layoutCitas = popupView.findViewById(R.id.layout_citas);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        if (tvTotalCitas == null || layoutCitas == null || btnClose == null) {
            return;
        }

        // Referencia a la base de datos Firebase
        DatabaseReference citasRef = FirebaseDatabase.getInstance().getReference("citas");

        citasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                layoutCitas.removeAllViews(); // Limpiar la lista antes de agregar nuevas citas

                int totalCitas = 0;
                LayoutInflater inflater = LayoutInflater.from(context);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String fecha = snapshot.child("fecha").getValue(String.class);
                    String hora = snapshot.child("hora").getValue(String.class);
                    String ubicacion = snapshot.child("ubicacion").getValue(String.class);

                    if (fecha != null && hora != null && ubicacion != null) {
                        // Inflar el diseño de la tarjeta card_cita.xml
                        View cardView = inflater.inflate(R.layout.card_cita, layoutCitas, false);

                        // Obtener referencias de los TextView en card_cita.xml
                        TextView tvFecha = cardView.findViewById(R.id.tvFecha);
                        TextView tvHora = cardView.findViewById(R.id.tvHora);
                        TextView tvUbicacion = cardView.findViewById(R.id.tvUbicacion);

                        // Asignar valores obtenidos de Firebase
                        tvFecha.setText("Fecha: " + fecha);
                        tvHora.setText("Hora: " + hora);
                        tvUbicacion.setText("Ubicación: " + ubicacion);

                        // Agregar la tarjeta al layout dinámico
                        layoutCitas.addView(cardView);
                        totalCitas++;
                    }
                }

                // Actualizar el número total de citas pendientes
                tvTotalCitas.setText("Total de citas pendientes: " + totalCitas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvTotalCitas.setText("Error al cargar las citas.");
            }
        });

        // Configurar el botón de cierre
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Mostrar la ventana emergente
        dialog.show();
    }

    // Métodos de ventanas emergentes para otras tarjetas
    private void mostrarVentanaEmergente(Context context) {
        mostrarDialogoBasico(context, R.layout.cuidadores_activos);
    }

    private void mostrarVentanaNotificacionesRecientes(Context context) {
        mostrarDialogoBasico(context, R.layout.notificaciones_recientes);
    }

    private void mostrarVentanaServiciosCompletados(Context context) {
        mostrarDialogoBasico(context, R.layout.servicios_completados);
    }

    private void mostrarDialogoBasico(Context context, int layout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        Button btnClose = popupView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
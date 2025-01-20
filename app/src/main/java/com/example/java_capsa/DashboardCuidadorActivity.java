package com.example.java_capsa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardCuidadorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvClientName, tvAppointmentTime;
    private ImageView ivClientAvatar, btnLogout;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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

        // Configurar el botón de cerrar sesión
        btnLogout.setOnClickListener(view -> cerrarSesion());

        // Configurar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_dashboard);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_scan) {
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra("nombreCuidador", "Nombre Predeterminado"); // Cambiar por datos reales
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

        // Verificar permisos de ubicación
        checkLocationPermission();
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

    // Métodos para obtener ubicación en tiempo real
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
        }

        LatLng defaultLocation = new LatLng(37.7749, -122.4194);
        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("San Francisco"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            }
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }
}

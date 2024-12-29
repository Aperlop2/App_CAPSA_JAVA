package com.example.java_capsa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardCuidadorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_dashboard);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Inicializar el BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Configurar la navegación
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId(); // Obtener el ID del ítem seleccionado

            if (id == R.id.nav_home) {
                // Ya estás en Home
                return true;
            } else if (id == R.id.nav_scan) {
                // Navegar a pantalla de escaneo
                startActivity(new Intent(this, ScanActivity.class));
                return true;
            } else if (id == R.id.nav_appointments) {
                // Navegar a pantalla de citas
                startActivity(new Intent(this, AppointmentsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                // Navegar a pantalla de perfil
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            // Si no se selecciona ningún ítem válido
            return false;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        enableLocation();

        // Agregar una ubicación predeterminada (opcional)
        LatLng sanFrancisco = new LatLng(37.7749, -122.4194);
        googleMap.addMarker(new MarkerOptions()
                .position(sanFrancisco)
                .title("San Francisco"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanFrancisco, 12f));
    }

    // Habilitar la ubicación del usuario
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    // Manejar los resultados de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.java_capsa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapaTiempoReal extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private GoogleMap mMap;
    private Marker lastSelectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_tiempo_real);

        // Configurar el botón para centrar el mapa
        Button btnCentrarMapa = findViewById(R.id.btn_centrar_mapa);
        btnCentrarMapa.setOnClickListener(v -> centrarMapa());

        // Configurar el botón para regresar
        ImageButton btnBackMapa = findViewById(R.id.btn_back_mapa);
        btnBackMapa.setOnClickListener(v -> {
            Intent intent = new Intent(MapaTiempoReal.this, gestionadministradores.class);
            startActivity(intent);
            finish();
        });

        // Verificar y solicitar permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            solicitarPermisos();
        } else {
            inicializarMapa();
        }
    }

    private void solicitarPermisos() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void inicializarMapa() {
        // Cargar dinámicamente el mapa en el FrameLayout
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_mapa, mapFragment)
                .commit();

        // Configurar el callback para el mapa
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configurar el tipo de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Habilitar la ubicación del usuario si se otorgaron permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, "Permisos de ubicación no otorgados.", Toast.LENGTH_SHORT).show();
        }

        // Configurar adaptador de ventanas de información personalizada
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Configurar el listener de clics en los marcadores
        mMap.setOnMarkerClickListener(marker -> {
            lastSelectedMarker = marker;
            marker.showInfoWindow();
            return true;
        });

        // Añadir marcadores con datos personalizados
        addMarkers();
    }

    private void addMarkers() {
        // Marcador 1: Tatiana Jiménez Ramón
        LatLng sanPablo = new LatLng(19.27315, -98.97342); // Coordenadas de San Pablo Atlatlahuca
        mMap.addMarker(new MarkerOptions()
                .position(sanPablo)
                .title("Tatiana Jiménez Ramón")
                .snippet("Última evidencia"));

        // Marcador 2: Ejemplo 2
        LatLng ejemplo2 = new LatLng(19.432608, -99.133209); // Ciudad de México
        mMap.addMarker(new MarkerOptions()
                .position(ejemplo2)
                .title("María López")
                .snippet("Última evidencia"));

        // Marcador 3: Ejemplo 3
        LatLng ejemplo3 = new LatLng(19.504255, -99.14679);
        mMap.addMarker(new MarkerOptions()
                .position(ejemplo3)
                .title("Carlos Pérez")
                .snippet("Última evidencia"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanPablo, 10)); // Zoom ajustado
        lastSelectedMarker = mMap.addMarker(new MarkerOptions().position(sanPablo));
    }

    private void centrarMapa() {
        if (lastSelectedMarker != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastSelectedMarker.getPosition(), 10)); // Zoom ajustado
        } else {
            Toast.makeText(this, "No hay marcador seleccionado.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                inicializarMapa();
            } else {
                Toast.makeText(this, "Permisos de ubicación denegados.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Adaptador para ventanas de información personalizada
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;

        @SuppressLint("InflateParams")
        CustomInfoWindowAdapter() {
            mWindow = LayoutInflater.from(MapaTiempoReal.this).inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {
            ImageView imageView = view.findViewById(R.id.image_view);
            TextView title = view.findViewById(R.id.title);
            TextView details = view.findViewById(R.id.details);

            if (Objects.equals(marker.getTitle(), "Tatiana Jiménez Ramón")) {
                imageView.setImageResource(R.drawable.tatiana);
                title.setText(marker.getTitle());
                details.setText("Fecha: 03/01/25\nHora: 8:35\nEstado: Activo");
            } else if (Objects.equals(marker.getTitle(), "María López")) {
                imageView.setImageResource(R.drawable.maria);
                title.setText(marker.getTitle());
                details.setText("Fecha: 02/01/25\nHora: 9:00\nEstado: Activo");
            } else if (Objects.equals(marker.getTitle(), "Carlos Pérez")) {
                imageView.setImageResource(R.drawable.carlos);
                title.setText(marker.getTitle());
                details.setText("Fecha: 01/01/25\nHora: 7:30\nEstado: Inactivo");
            }
        }
    }
}

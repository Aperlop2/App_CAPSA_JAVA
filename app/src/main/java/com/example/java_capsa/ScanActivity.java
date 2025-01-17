package com.example.java_capsa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView locationTextView;
    private EditText descripcionEditText;
    private ImageView photoImageView;
    private Bitmap capturedPhoto;
    private String nombreCuidador = "";
    private FirebaseAuth mAuth;
    private DatabaseReference caretakerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        caretakerRef = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

        locationTextView = findViewById(R.id.locationTextView);
        descripcionEditText = findViewById(R.id.descripcionEditText);
        photoImageView = findViewById(R.id.photoImageView);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        Button enviarButton = findViewById(R.id.enviarButton);

        verificarAutenticacion();

        takePhotoButton.setOnClickListener(v -> openCamera());
        enviarButton.setOnClickListener(v -> enviarDatos());

        checkLocationPermission();
    }

    private void verificarAutenticacion() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginPrincipal.class);
            startActivity(intent);
            finish();
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            try {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir la cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enviarDatos() {
        String ubicacion = locationTextView.getText().toString();

        if (capturedPhoto == null || ubicacion.isEmpty() || ubicacion.equals("Esperando ubicación...") || descripcionEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos y asegúrese de que la ubicación esté disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = descripcionEditText.getText().toString();
        String fotoBase64 = bitmapToBase64(capturedPhoto);
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            caretakerRef.get().addOnSuccessListener(snapshot -> {
                boolean encontrado = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String storedEmail = child.child("correo").getValue(String.class);
                    if (storedEmail != null && storedEmail.equals(email)) {
                        String nombreCuidador = child.child("nombre").getValue(String.class);
                        if (nombreCuidador == null) {
                            nombreCuidador = "Cuidador sin nombre asignado";
                        }
                        enviarDatosAlServidor(nombreCuidador, ubicacion, descripcion, fotoBase64, fechaHora);
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    Toast.makeText(this, "No se encontró información para el cuidador con correo: " + email, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(this, "Error al conectar con Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Usuario no autenticado. Por favor, inicie sesión.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            locationTextView.setText(addresses.get(0).getAddressLine(0));
                        } else {
                            locationTextView.setText("No se encontró la dirección");
                        }
                    } catch (IOException e) {
                        locationTextView.setText("Error al obtener la dirección");
                    }
                } else {
                    locationTextView.setText("Esperando ubicación...");
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "Error de permisos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarDatosAlServidor(String nombreCuidador, String ubicacion, String descripcion, String fotoBase64, String fechaHora) {
        String url = "http://192.168.100.5/guardar_evidencia.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre_cuidador", nombreCuidador);
                params.put("ubicacion", ubicacion);
                params.put("descripcion", descripcion);
                params.put("foto", fotoBase64);
                params.put("fecha_hora", fechaHora);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            capturedPhoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            photoImageView.setImageBitmap(capturedPhoto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

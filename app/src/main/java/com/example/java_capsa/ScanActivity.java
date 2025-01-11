package com.example.java_capsa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView locationTextView;
    private EditText descripcionEditText;
    private ImageView photoImageView;
    private Bitmap capturedPhoto;
    private String nombreCuidador = "Nombre Predeterminado"; // Valor por defecto
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        // Listener para cambios de estado de autenticación
        auth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "Usuario no autenticado. Inicie sesión.", Toast.LENGTH_SHORT).show();
                redirigirALogin();
            } else {
                Log.d("AUTH_STATE", "Usuario autenticado: " + currentUser.getUid());
                obtenerNombreCuidador(); // Asegura que los datos sean recuperados correctamente
            }
        });

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        locationTextView = findViewById(R.id.locationTextView);
        descripcionEditText = findViewById(R.id.descripcionEditText);
        photoImageView = findViewById(R.id.photoImageView);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        Button enviarButton = findViewById(R.id.enviarButton);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        takePhotoButton.setOnClickListener(v -> openCamera());
        enviarButton.setOnClickListener(v -> enviarDatos());

        checkLocationPermission();
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
        if (capturedPhoto == null || locationTextView.getText().toString().isEmpty() || descripcionEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado. Inicie sesión.", Toast.LENGTH_SHORT).show();
            redirigirALogin();
            return;
        }

        String ubicacion = locationTextView.getText().toString();
        String descripcion = descripcionEditText.getText().toString();
        String fotoBase64 = bitmapToBase64(capturedPhoto);

        enviarDatosAlServidor(nombreCuidador, ubicacion, descripcion, fotoBase64);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
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
                    Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "Error al obtener ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerNombreCuidador() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference caretakerRef = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

            caretakerRef.child(userId).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    if (nombre != null) {
                        nombreCuidador = nombre;
                        Log.d("FIREBASE_DATA", "Nombre del cuidador actualizado: " + nombreCuidador);
                    } else {
                        Log.w("FIREBASE_WARNING", "Campo 'nombre' no encontrado.");
                    }
                } else {
                    Log.e("FIREBASE_ERROR", "Datos no encontrados para UID: " + userId);
                    Toast.makeText(this, "No se encontraron datos del cuidador.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("FIREBASE_ERROR", "Error al obtener datos: " + e.getMessage(), e);
                Toast.makeText(this, "Error al conectar con Firebase.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Usuario no autenticado. Inicie sesión.", Toast.LENGTH_SHORT).show();
            redirigirALogin();
        }
    }

    private void enviarDatosAlServidor(String nombreCuidador, String ubicacion, String descripcion, String fotoBase64) {
        String url = "http://192.168.100.5/guardar_evidencia.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show(),
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        Toast.makeText(this, "Error: Código HTTP " + statusCode, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre_cuidador", nombreCuidador);
                params.put("ubicacion", ubicacion);
                params.put("descripcion", descripcion);
                params.put("foto", fotoBase64);
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

    private void redirigirALogin() {
        Intent intent = new Intent(this, LoginPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            capturedPhoto = (Bitmap) data.getExtras().get("data");
            photoImageView.setImageBitmap(capturedPhoto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

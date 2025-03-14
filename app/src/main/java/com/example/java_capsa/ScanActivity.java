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
    private FirebaseAuth mAuth;
    private DatabaseReference caretakerRef;
    private String nombreCuidador = "", emailUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Inicialización de Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        caretakerRef = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

        // Inicialización de vistas
        locationTextView = findViewById(R.id.locationTextView);
        descripcionEditText = findViewById(R.id.descripcionEditText);
        photoImageView = findViewById(R.id.photoImageView);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        Button enviarButton = findViewById(R.id.enviarButton);

        // Verificar autenticación y obtener datos del cuidador
        verificarAutenticacion();

        // Configurar listeners para botones
        takePhotoButton.setOnClickListener(v -> openCamera());
        enviarButton.setOnClickListener(v -> enviarDatos());

        checkLocationPermission();

        Button btnDashboard = findViewById(R.id.btnDashboard);
        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(ScanActivity.this, DashboardCuidadorActivity.class);
            startActivity(intent);
        });
    }

    private void enviarDatos() {
        String ubicacion = locationTextView.getText().toString();

        // Verificar que todos los campos estén completos
        if (capturedPhoto == null || ubicacion.isEmpty() || ubicacion.equals("Esperando ubicación...") || descripcionEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir la foto a Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        capturedPhoto.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String fotoBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        String descripcion = descripcionEditText.getText().toString();
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            caretakerRef.get().addOnSuccessListener(snapshot -> {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String storedEmail = child.child("correo").getValue(String.class);
                    if (storedEmail != null && storedEmail.equals(email)) {
                        String nombreCuidador = child.child("nombre").getValue(String.class);
                        if (nombreCuidador == null) {
                            nombreCuidador = "Cuidador sin nombre asignado";
                        }

                        // Ahora que tienes el nombre del cuidador, puedes enviar los datos
                        enviarDatosAlServidor(nombreCuidador, ubicacion, descripcion, fotoBase64, fechaHora);
                        break;
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(this, "Error al conectar con Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Usuario no autenticado. Inicie sesión.", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarAutenticacion() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            emailUsuario = currentUser.getEmail();
            if (emailUsuario != null) {
                obtenerDatosCuidador(emailUsuario);
            } else {
                mostrarError("Error al obtener el correo del usuario.");
            }
        } else {
            mostrarError("Usuario no autenticado. Inicie sesión.");
        }
    }

    private void obtenerDatosCuidador(String email) {
        caretakerRef.orderByChild("correo").equalTo(email).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    nombreCuidador = child.child("nombre").getValue(String.class);
                    if (nombreCuidador == null) {
                        nombreCuidador = "Cuidador sin nombre asignado";
                    }
                    Toast.makeText(this, "Bienvenido, " + nombreCuidador, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                mostrarError("No se encontró información del cuidador.");
            }
        }).addOnFailureListener(e -> mostrarError("Error al conectar con Firebase: " + e.getMessage()));
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginPrincipal.class));
        finish();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        locationTextView.setText((addresses != null && !addresses.isEmpty()) ? addresses.get(0).getAddressLine(0) : "Ubicación desconocida");
                    } catch (IOException e) {
                        locationTextView.setText("Error al obtener la dirección");
                    }
                } else {
                    locationTextView.setText("Esperando ubicación...");
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarDatosAlServidor(String nombreCuidador, String ubicacion, String descripcion, String fotoBase64, String fechaHora) {
        String url = "http://192.168.100.17/guardar_evidencia.php";

        // 🔴 Agregar logs para verificar si los datos se están generando correctamente antes de enviarlos
        Log.d("DATOS_ENVIO", "Nombre Cuidador: " + nombreCuidador);
        Log.d("DATOS_ENVIO", "Ubicación: " + ubicacion);
        Log.d("DATOS_ENVIO", "Descripción: " + descripcion);
        Log.d("DATOS_ENVIO", "Foto (Base64): " + fotoBase64.substring(0, 30) + "..."); // Solo mostramos una parte por seguridad
        Log.d("DATOS_ENVIO", "Fecha y Hora: " + fechaHora); // Aquí verificamos si se está enviando

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("RESPUESTA_SERVIDOR", "Respuesta: " + response);
                    Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();

                    descripcionEditText.setText("");
                },
                error -> {
                    Log.e("ERROR_VOLLEY", "Error de red: " + error.getMessage());
                    Toast.makeText(this, "Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
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
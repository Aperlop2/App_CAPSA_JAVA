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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST_CODE = 3;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView locationTextView;
    private ImageView photoImageView;
    private Bitmap capturedPhoto;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        locationTextView = findViewById(R.id.locationTextView);
        photoImageView = findViewById(R.id.photoImageView);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        Button enviarButton = findViewById(R.id.enviarButton);

        // Inicializar el cliente de ubicación
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        databaseHelper = new DatabaseHelper(this); // Inicializar base de datos

        // Verificar permisos de ubicación
        checkLocationPermission();

        // Configurar botón para tomar fotos
        takePhotoButton.setOnClickListener(v -> checkCameraPermission());

        // Configurar botón para enviar datos
        enviarButton.setOnClickListener(v -> {
            String nombreCuidador = "Nombre Predeterminado"; // Cambiar por el nombre dinámico en el futuro
            String ubicacion = locationTextView.getText().toString();
            String descripcion = ((EditText) findViewById(R.id.descripcionEditText)).getText().toString();

            if (capturedPhoto == null || ubicacion.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir la foto a Base64
            String fotoBase64 = bitmapToBase64(capturedPhoto);

            long result = databaseHelper.insertarEvidencia(nombreCuidador, ubicacion, descripcion, fotoBase64);

            if (result != -1) {
                Toast.makeText(this, "Evidencia guardada exitosamente", Toast.LENGTH_SHORT).show();
                // Limpiar los campos después de guardar
                locationTextView.setText("");
                ((EditText) findViewById(R.id.descripcionEditText)).setText("");
                photoImageView.setImageBitmap(null);
                capturedPhoto = null;
            } else {
                Toast.makeText(this, "Error al guardar la evidencia", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permisos concedidos
            getLocation();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permiso concedido, abrir cámara
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    @SuppressLint("SetTextI18n")
    private void getLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Convertir coordenadas en dirección usando Geocoder
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String fullAddress = address.getAddressLine(0);
                                    locationTextView.setText(fullAddress);
                                } else {
                                    locationTextView.setText("No se pudo determinar la dirección");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                locationTextView.setText("Error al obtener la dirección");
                            }

                        } else {
                            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
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
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            capturedPhoto = (Bitmap) data.getExtras().get("data");
            capturedPhoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            photoImageView.setImageBitmap(capturedPhoto);
        }
    }
}

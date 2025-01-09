package com.example.java_capsa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AgregarNuevoCuidador extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView cameraIcon;
    private EditText etNombre, etDireccion, etTelefono, etHorario;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_nuevo_cuidador);

        cameraIcon = findViewById(R.id.camera_icon);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        etHorario = findViewById(R.id.etHorario);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // Abrir la galería al hacer clic en el ícono de la cámara
        cameraIcon.setOnClickListener(v -> openGallery());

        // Acción para el botón Guardar
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String direccion = etDireccion.getText().toString();
            String telefono = etTelefono.getText().toString();
            String horario = etHorario.getText().toString();

            if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || horario.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Por favor llena todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviar los datos a la pantalla de "Gestión de Cuidadores"
            Intent intent = new Intent(this, GestionDeCuidadores.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("direccion", direccion);
            intent.putExtra("telefono", telefono);
            intent.putExtra("horario", horario);
            intent.putExtra("imageUri", selectedImageUri.toString());
            startActivity(intent);
            finish();
        });

        // Acción para el botón Cancelar
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Mostrar la imagen seleccionada
            cameraIcon.setImageURI(selectedImageUri);
        }
    }
}
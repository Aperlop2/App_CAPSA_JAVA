package com.example.java_capsa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AgregarNuevoCuidadorActivity extends AppCompatActivity {

    private DatabaseReference solicitudesRef;
    private DatabaseReference cuidadoresRef;

    private TextView nombreTextView, correoTextView, telefonoTextView, especialidadTextView, fechaNacimientoTextView, horariosDisponiblesTextView;

    private String cuidadorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_nuevo_cuidador);

        // Recibir el ID del cuidador desde el Intent
        cuidadorId = getIntent().getStringExtra("caregiver_id");

        if (cuidadorId == null || cuidadorId.isEmpty()) {
            Toast.makeText(this, "Error: ID del cuidador no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        solicitudesRef = FirebaseDatabase.getInstance().getReference("solicitudes_cuidadores");
        cuidadoresRef = FirebaseDatabase.getInstance().getReference("usuarios/cuidadores");

        nombreTextView = findViewById(R.id.nombreTextView);
        correoTextView = findViewById(R.id.correoTextView);
        telefonoTextView = findViewById(R.id.telefonoTextView);
        especialidadTextView = findViewById(R.id.especialidadTextView);
        fechaNacimientoTextView = findViewById(R.id.fechaNacimientoTextView);
        horariosDisponiblesTextView = findViewById(R.id.horariosDisponiblesTextView);

        Button btnAceptar = findViewById(R.id.btnGuardar);
        Button btnRechazar = findViewById(R.id.btnCancelar);

        cargarDatosCuidador();

        btnAceptar.setOnClickListener(v -> aceptarCuidador());
        btnRechazar.setOnClickListener(v -> rechazarCuidador());
    }

    private void cargarDatosCuidador() {
        solicitudesRef.child(cuidadorId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();

                String nombre = snapshot.child("nombre").getValue(String.class);
                String correo = snapshot.child("correo").getValue(String.class);
                String telefono = snapshot.child("telefono").getValue(String.class);
                String especialidad = snapshot.child("especialidad").getValue(String.class);
                String fechaNacimiento = snapshot.child("fechaNacimiento").getValue(String.class);
                String horariosDisponibles = snapshot.child("horariosDisponibles").getValue(String.class);

                nombreTextView.setText(nombre != null ? nombre : "N/A");
                correoTextView.setText(correo != null ? correo : "N/A");
                telefonoTextView.setText(telefono != null ? telefono : "N/A");
                especialidadTextView.setText(especialidad != null ? especialidad : "N/A");
                fechaNacimientoTextView.setText(fechaNacimiento != null ? fechaNacimiento : "N/A");
                horariosDisponiblesTextView.setText(horariosDisponibles != null ? horariosDisponibles : "N/A");
            } else {
                Toast.makeText(this, "Error al cargar datos del cuidador", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aceptarCuidador() {
        solicitudesRef.child(cuidadorId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                Map<String, Object> data = (Map<String, Object>) snapshot.getValue();

                if (data != null) {
                    // Actualizar estado a "revisado"
                    solicitudesRef.child(cuidadorId).child("estado").setValue("revisado");

                    // Encriptar contraseña antes de guardar en "cuidadores"
                    String rawPassword = (String) data.get("contraseña");
                    String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

                    // Transferir los datos necesarios a "cuidadores"
                    Map<String, Object> filteredData = new HashMap<>();
                    filteredData.put("nombre", data.get("nombre"));
                    filteredData.put("correo", data.get("correo"));
                    filteredData.put("telefono", data.get("telefono"));
                    filteredData.put("fechaNacimiento", data.get("fechaNacimiento"));
                    filteredData.put("especialidad", data.get("especialidad"));
                    filteredData.put("horariosDisponibles", data.get("horariosDisponibles"));
                    filteredData.put("contraseña", hashedPassword);

                    // Agregar el correo a Firebase Authentication
                    String correo = (String) data.get("correo");
                    if (correo != null && !correo.isEmpty()) {
                        agregarCorreoAutenticacion(correo, rawPassword);
                    }

                    // Guardar en la base de datos "cuidadores"
                    cuidadoresRef.child(cuidadorId).setValue(filteredData);
                    enviarDatosAMySQL(filteredData);

                    Toast.makeText(this, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Error al aceptar la solicitud", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rechazarCuidador() {
        solicitudesRef.child(cuidadorId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al rechazar la solicitud", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarCorreoAutenticacion(String correo, String rawPassword) {
        // Agregar el correo al sistema de autenticación de Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(correo, rawPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // El cuidador ha sido agregado correctamente
                        FirebaseAuth.getInstance().getCurrentUser(); // Ahora está autenticado
                        Toast.makeText(this, "Cuidador añadido correctamente a la autenticación", Toast.LENGTH_SHORT).show();
                    } else {
                        // Si falla la creación de la cuenta
                        Toast.makeText(this, "Error al agregar el correo a la autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enviarDatosAMySQL(Map<String, Object> data) {
        String url = "http://192.168.137.1/evidencias/guardar_cuidador.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Guardado en MySQL", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error al guardar en MySQL: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", Objects.requireNonNull(data.get("nombre")).toString());
                params.put("correo", Objects.requireNonNull(data.get("correo")).toString());
                params.put("telefono", Objects.requireNonNull(data.get("telefono")).toString());
                params.put("fechaNacimiento", Objects.requireNonNull(data.get("fechaNacimiento")).toString());
                params.put("especialidad", Objects.requireNonNull(data.get("especialidad")).toString());
                params.put("horariosDisponibles", Objects.requireNonNull(data.get("horariosDisponibles")).toString());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}


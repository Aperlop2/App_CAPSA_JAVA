package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class gestionadministradores extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_administradores);

        // Configuración del clic en la tarjeta de "Cuidadores Activos"
        findViewById(R.id.card_cuidadores_activos).setOnClickListener(v -> mostrarVentanaEmergente(this));

        // Configuración del clic en la tarjeta de "Citas Pendientes"
        findViewById(R.id.card_citas_pendientes).setOnClickListener(v -> mostrarVentanaCitasPendientes(this));
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaEmergente(Context context) {
        // Inflar el diseño de la ventana emergente
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.cuidadores_activos, null);

        // Crear el diálogo para la ventana emergente
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Configurar los elementos de la ventana emergente
        TextView tvTotalCuidadores = popupView.findViewById(R.id.tv_total_cuidadores);
        ScrollView scrollCuidadores = popupView.findViewById(R.id.scroll_cuidadores);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        // Mostrar el número de cuidadores activos (Placeholder)
        String totalCuidadores = String.format(getString(R.string.numero_cuidadores_placeholder), 0);
        tvTotalCuidadores.setText(totalCuidadores);

        // Contenedor dinámico de tarjetas
        LinearLayout layoutCuidadores = new LinearLayout(context);
        layoutCuidadores.setOrientation(LinearLayout.VERTICAL);
        scrollCuidadores.addView(layoutCuidadores);

        // Ejemplo de datos estáticos para mostrar el esqueleto
        for (int i = 1; i <= 3; i++) {
            // Inflar cada tarjeta desde el diseño `card_cuidador.xml`
            View cardView = inflater.inflate(R.layout.card_cuidador, layoutCuidadores, false);

            // Configurar los elementos de la tarjeta (Placeholder)
            TextView tvName = cardView.findViewById(R.id.tv_name);
            TextView tvStatus = cardView.findViewById(R.id.tv_status);
            TextView tvLocation = cardView.findViewById(R.id.tv_location);
            TextView tvEspecialidad = cardView.findViewById(R.id.tv_especialidad);

            tvName.setText("Nombre:");
            tvStatus.setText("Estado:");
            tvLocation.setText("Ubicación:");
            tvEspecialidad.setText("Especialidad:");

            // Agregar la tarjeta al contenedor dinámico
            layoutCuidadores.addView(cardView);
        }

        // Configurar el botón de cierre de la ventana emergente
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaCitasPendientes(Context context) {
        // Inflar el diseño de la ventana emergente
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.citas_pendientes, null);

        // Crear el diálogo para la ventana emergente
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Configurar los elementos de la ventana emergente
        TextView tvTotalCitas = popupView.findViewById(R.id.tv_total_citas);
        ScrollView scrollCitas = popupView.findViewById(R.id.scroll_citas);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        // Mostrar el número de citas pendientes (Placeholder)
        String totalCitas = String.format(getString(R.string.total_citas_placeholder), 0);
        tvTotalCitas.setText(totalCitas);

        // Contenedor dinámico de tarjetas
        LinearLayout layoutCitas = new LinearLayout(context);
        layoutCitas.setOrientation(LinearLayout.VERTICAL);
        scrollCitas.addView(layoutCitas);

        // Ejemplo de datos estáticos para mostrar el esqueleto
        for (int i = 1; i <= 3; i++) {
            // Inflar cada tarjeta desde el diseño `card_cita.xml`
            View cardView = inflater.inflate(R.layout.card_cita, layoutCitas, false);

            // Configurar los elementos de la tarjeta (Placeholder)
            TextView tvCita = cardView.findViewById(R.id.tv_cita);
            TextView tvFecha = cardView.findViewById(R.id.tv_fecha);
            TextView tvCuidador = cardView.findViewById(R.id.tv_cuidador);
            TextView tvUbicacion = cardView.findViewById(R.id.tv_ubicacion);

            tvCita.setText("Cita:");
            tvFecha.setText("Fecha:");
            tvCuidador.setText("Cuidador:");
            tvUbicacion.setText("Ubicación:");

            // Agregar la tarjeta al contenedor dinámico
            layoutCitas.addView(cardView);
        }

        // Configurar el botón de cierre de la ventana emergente
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }
}

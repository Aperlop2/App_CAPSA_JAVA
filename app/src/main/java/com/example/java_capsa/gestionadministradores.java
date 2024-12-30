package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

        // Configuración del clic en la tarjeta de "Notificaciones Recientes"
        findViewById(R.id.card_notificaciones_recientes).setOnClickListener(v -> mostrarVentanaNotificacionesRecientes(this));

        // Configuración del clic en la tarjeta de "Servicios Completados"
        findViewById(R.id.card_servicios_completados).setOnClickListener(v -> mostrarVentanaServiciosCompletados(this));

        // Configuración del clic en el icono del mapa
        ImageView iconMap = findViewById(R.id.icon_map);
        iconMap.setOnClickListener(v -> {
            // Iniciar la actividad de MapaTiempoReal
            Intent intent = new Intent(gestionadministradores.this, MapaTiempoReal.class);
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaEmergente(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.cuidadores_activos, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        TextView tvTotalCuidadores = popupView.findViewById(R.id.tv_total_cuidadores);
        ScrollView scrollCuidadores = popupView.findViewById(R.id.scroll_cuidadores);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        String totalCuidadores = String.format(getString(R.string.numero_cuidadores_placeholder), 0);
        tvTotalCuidadores.setText(totalCuidadores);

        LinearLayout layoutCuidadores = new LinearLayout(context);
        layoutCuidadores.setOrientation(LinearLayout.VERTICAL);
        scrollCuidadores.addView(layoutCuidadores);

        for (int i = 1; i <= 3; i++) {
            View cardView = inflater.inflate(R.layout.card_cuidador, layoutCuidadores, false);

            TextView tvName = cardView.findViewById(R.id.tv_name);
            TextView tvStatus = cardView.findViewById(R.id.tv_status);
            TextView tvLocation = cardView.findViewById(R.id.tv_location);
            TextView tvEspecialidad = cardView.findViewById(R.id.tv_especialidad);

            tvName.setText("Nombre:");
            tvStatus.setText("Estado:");
            tvLocation.setText("Ubicación:");
            tvEspecialidad.setText("Especialidad:");

            layoutCuidadores.addView(cardView);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaCitasPendientes(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.citas_pendientes, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        TextView tvTotalCitas = popupView.findViewById(R.id.tv_total_citas);
        ScrollView scrollCitas = popupView.findViewById(R.id.scroll_citas);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        String totalCitas = String.format(getString(R.string.total_citas_placeholder), 0);
        tvTotalCitas.setText(totalCitas);

        LinearLayout layoutCitas = new LinearLayout(context);
        layoutCitas.setOrientation(LinearLayout.VERTICAL);
        scrollCitas.addView(layoutCitas);

        for (int i = 1; i <= 3; i++) {
            View cardView = inflater.inflate(R.layout.card_cita, layoutCitas, false);

            TextView tvCita = cardView.findViewById(R.id.tv_cita);
            TextView tvFecha = cardView.findViewById(R.id.tv_fecha);
            TextView tvCuidador = cardView.findViewById(R.id.tv_cuidador);
            TextView tvUbicacion = cardView.findViewById(R.id.tv_ubicacion);

            tvCita.setText("Cita:");
            tvFecha.setText("Fecha:");
            tvCuidador.setText("Cuidador:");
            tvUbicacion.setText("Ubicación:");

            layoutCitas.addView(cardView);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaNotificacionesRecientes(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.notificaciones_recientes, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        TextView tvTotalNotificaciones = popupView.findViewById(R.id.tv_total_notificaciones);
        ScrollView scrollNotificaciones = popupView.findViewById(R.id.scroll_notificaciones);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String totalNotificaciones = String.format(getString(R.string.total_de_notificaciones_recientes_0), 0);
        tvTotalNotificaciones.setText(totalNotificaciones);

        LinearLayout layoutNotificaciones = new LinearLayout(context);
        layoutNotificaciones.setOrientation(LinearLayout.VERTICAL);
        scrollNotificaciones.addView(layoutNotificaciones);

        for (int i = 1; i <= 3; i++) {
            View cardView = inflater.inflate(R.layout.card_notificacion, layoutNotificaciones, false);

            TextView tvFecha = cardView.findViewById(R.id.tv_fecha);
            TextView tvTipo = cardView.findViewById(R.id.tv_tipo_notificacion);
            TextView tvNombre = cardView.findViewById(R.id.tv_nombre);
            TextView tvDireccion = cardView.findViewById(R.id.tv_direccion);
            TextView tvHorario = cardView.findViewById(R.id.tv_horario);

            tvFecha.setText("Fecha:");
            tvTipo.setText("Tipo de notificación:");
            tvNombre.setText("Nombre:");
            tvDireccion.setText("Dirección:");
            tvHorario.setText("Horario:");

            layoutNotificaciones.addView(cardView);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaServiciosCompletados(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.servicios_completados, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        TextView tvTotalServicios = popupView.findViewById(R.id.tv_total_servicios);
        ScrollView scrollServicios = popupView.findViewById(R.id.scroll_servicios);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        String totalServicios = String.format(getString(R.string.total_servicios_placeholder), 0);
        tvTotalServicios.setText(totalServicios);

        LinearLayout layoutServicios = new LinearLayout(context);
        layoutServicios.setOrientation(LinearLayout.VERTICAL);
        scrollServicios.addView(layoutServicios);

        for (int i = 1; i <= 3; i++) {
            View cardView = inflater.inflate(R.layout.card_servicio, layoutServicios, false);

            TextView tvServicio = cardView.findViewById(R.id.tv_servicio);
            TextView tvFecha = cardView.findViewById(R.id.tv_fecha);
            TextView tvHora = cardView.findViewById(R.id.tv_hora);
            TextView tvCuidador = cardView.findViewById(R.id.tv_cuidador);
            TextView tvUbicacion = cardView.findViewById(R.id.tv_ubicacion);

            tvServicio.setText("Servicio:");
            tvFecha.setText("Fecha:");
            tvHora.setText("Hora:");
            tvCuidador.setText("Cuidador:");
            tvUbicacion.setText("Ubicación:");

            layoutServicios.addView(cardView);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}

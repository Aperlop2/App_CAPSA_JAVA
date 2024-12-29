package com.example.java_capsa;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_administradores);

        // Configuración del clic en la tarjeta de "Cuidadores Activos"
        findViewById(R.id.card_cuidadores_activos).setOnClickListener(v -> mostrarVentanaEmergente(this));
    }

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
        String totalCuidadores = getString(R.string.numero_cuidadores_placeholder, 0);
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

            tvName.setText(getString(R.string.nombre_placeholder, "Cuidador " + i));
            tvStatus.setText(getString(R.string.estado_placeholder, "Activo"));
            tvLocation.setText(getString(R.string.ubicacion_placeholder, "Pendiente"));
            tvEspecialidad.setText(getString(R.string.especialidad_placeholder, "General"));

            // Agregar la tarjeta al contenedor dinámico
            layoutCuidadores.addView(cardView);
        }

        // Configurar el botón de cierre de la ventana emergente
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }
}

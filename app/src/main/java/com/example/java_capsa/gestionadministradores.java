package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class gestionadministradores extends AppCompatActivity {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "dark_theme";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cargar el tema desde las preferencias
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(KEY_THEME, false);
        if (isDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_administradores);

        // Configuración del clic en la tarjeta de "Notificaciones Recientes"
        findViewById(R.id.card_notificaciones_recientes).setOnClickListener(v -> mostrarVentanaNotificacionesRecientes(this));

        // Configuración del clic en el botón de cambio de tema
        findViewById(R.id.btn_theme_toggle).setOnClickListener(v -> toggleTheme());

        // Configuración del botón "Regresar"
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            Intent intent = new Intent(gestionadministradores.this, LoginPrincipal.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual para evitar volver con el botón de retroceso
        });
    }

    private void toggleTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(KEY_THEME, false);

        SharedPreferences.Editor editor = preferences.edit();
        if (isDarkTheme) {
            editor.putBoolean(KEY_THEME, false);
            setThemeForAllActivities(R.style.AppTheme_Light);
        } else {
            editor.putBoolean(KEY_THEME, true);
            setThemeForAllActivities(R.style.AppTheme_Dark);
        }
        editor.apply();

        // Reiniciar la actividad para aplicar el cambio de tema
        recreate();
    }

    private void setThemeForAllActivities(int themeResId) {
        AppCompatDelegate.setDefaultNightMode(
                themeResId == R.style.AppTheme_Dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @SuppressLint("SetTextI18n")
    private void mostrarVentanaNotificacionesRecientes(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.notificaciones_recientes, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        RecyclerView rvNotificaciones = popupView.findViewById(R.id.rv_notificaciones);
        Button btnClose = popupView.findViewById(R.id.btn_close);

        // Configurar RecyclerView
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(context));
        NotificacionesAdapter adapter = new NotificacionesAdapter(obtenerListaNotificaciones());
        rvNotificaciones.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Método para obtener datos simulados
    private List<Notificacion> obtenerListaNotificaciones() {
        List<Notificacion> notificaciones = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            notificaciones.add(new Notificacion("Mensaje " + i, "Hora " + i + ":00"));
        }
        return notificaciones;
    }
}

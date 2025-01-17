package com.example.java_capsa;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MiApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);

        // Habilitar persistencia de sesión en Firebase Authentication
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);    }
}
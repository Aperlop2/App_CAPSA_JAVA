package com.example.java_capsa;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "evidencias.db";
    private static final int DATABASE_VERSION = 2; // Incrementa la versión

    public static final String TABLE_NAME = "evidencias";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE_CUIDADOR = "nombre_cuidador";
    public static final String COLUMN_UBICACION = "ubicacion";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_FOTO = "foto";
    public static final String COLUMN_FECHA_HORA = "fecha_hora"; // Nueva columna

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE_CUIDADOR + " TEXT, " +
                COLUMN_UBICACION + " TEXT, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_FOTO + " TEXT, " +
                COLUMN_FECHA_HORA + " DATETIME DEFAULT CURRENT_TIMESTAMP)"; // Agrega la columna fecha_hora
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_FECHA_HORA + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        }
    }

    public long insertarEvidencia(String nombreCuidador, String ubicacion, String descripcion, String fotoBase64) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOMBRE_CUIDADOR, nombreCuidador);
        values.put(COLUMN_UBICACION, ubicacion);
        values.put(COLUMN_DESCRIPCION, descripcion);
        values.put(COLUMN_FOTO, fotoBase64);
        // No es necesario agregar fecha_hora, SQLite lo maneja automáticamente con el valor por defecto.

        return db.insert(TABLE_NAME, null, values);
    }
}

package com.example.java_capsa;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "evidencias_db";
    private static final int DATABASE_VERSION = 1; // Versión simplificada

    public static final String TABLE_NAME = "evidencias";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE_CUIDADOR = "nombre_cuidador";
    public static final String COLUMN_UBICACION = "ubicacion";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_FOTO = "foto";
    public static final String COLUMN_FECHA_HORA = "fecha_hora";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE_CUIDADOR + " TEXT NOT NULL, " +
                COLUMN_UBICACION + " TEXT NOT NULL, " +
                COLUMN_DESCRIPCION + " TEXT NOT NULL, " +
                COLUMN_FOTO + " TEXT NOT NULL, " +
                COLUMN_FECHA_HORA + " TEXT NOT NULL)"; // Fecha y hora en formato "YYYY-MM-DD HH:MM:SS"
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertarEvidencia(String nombreCuidador, String ubicacion, String descripcion, String fotoBase64, String fechaHora) {
        // Validar datos antes de insertar
        if (nombreCuidador == null || nombreCuidador.isEmpty() ||
                ubicacion == null || ubicacion.isEmpty() ||
                descripcion == null || descripcion.isEmpty() ||
                fotoBase64 == null || fotoBase64.isEmpty() ||
                fechaHora == null || fechaHora.isEmpty()) {
            return -1; // Indica que los datos no son válidos
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOMBRE_CUIDADOR, nombreCuidador);
        values.put(COLUMN_UBICACION, ubicacion);
        values.put(COLUMN_DESCRIPCION, descripcion);
        values.put(COLUMN_FOTO, fotoBase64);
        values.put(COLUMN_FECHA_HORA, fechaHora);

        long resultado = db.insert(TABLE_NAME, null, values);
        db.close(); // Cerrar la conexión después de la inserción
        return resultado;
    }

    public boolean eliminarEvidencia(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasAfectadas = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close(); // Cerrar la conexión después de la eliminación
        return filasAfectadas > 0;
    }
}
package com.example.java_capsa;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "evidencias.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "evidencias";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE_CUIDADOR = "nombre_cuidador";
    public static final String COLUMN_UBICACION = "ubicacion";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_FOTO = "foto";

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
                COLUMN_FOTO + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertarEvidencia(String nombreCuidador, String ubicacion, String descripcion, String fotoBase64) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre_cuidador", nombreCuidador);
        values.put("ubicacion", ubicacion);
        values.put("descripcion", descripcion);
        values.put("foto", fotoBase64);

        return db.insert("evidencias", null, values);
    }

}

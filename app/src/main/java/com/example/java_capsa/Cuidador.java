package com.example.java_capsa;

public class Cuidador {
    private int id;
    private String nombre;
    private String ubicacion;
    private String descripcion;
    private String fotoUrl;
    private String fechaHora;

    public Cuidador(int id, String nombre, String ubicacion, String descripcion, String fotoUrl, String fechaHora) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.fotoUrl = fotoUrl;
        this.fechaHora = fechaHora;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getFechaHora() {
        return fechaHora;
    }
}

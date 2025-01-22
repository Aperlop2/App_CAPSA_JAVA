package com.example.java_capsa;

public class Cuidador1 {

    private int id;
    private String nombre;
    private String ubicacion;
    private String descripcion;
    private String foto;
    private String fechaHora;

    public Cuidador1(int id, String nombre, String ubicacion, String descripcion, String foto, String fechaHora) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.foto = foto;
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

    public String getFoto() {
        return foto;
    }

    public String getFechaHora() {
        return fechaHora;
    }
}

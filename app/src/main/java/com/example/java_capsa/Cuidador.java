package com.example.java_capsa;

public class Cuidador {
    private final String nombre;
    private final String direccion;
    private final String telefono;
    private final String horario;
    private final String imageUri;

    public Cuidador(String nombre, String direccion, String telefono, String horario, String imageUri) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.horario = horario;
        this.imageUri = imageUri;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getHorario() {
        return horario;
    }

    public String getImageUri() {
        return imageUri;
    }
}
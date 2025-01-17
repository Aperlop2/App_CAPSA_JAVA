package com.example.java_capsa;

public class Cita {
    private String detalle;
    private String cuidador;
    private String ubicacion;

    public Cita(String detalle, String cuidador, String ubicacion) {
        this.detalle = detalle;
        this.cuidador = cuidador;
        this.ubicacion = ubicacion;
    }

    // Getters
    public String getDetalle() {
        return detalle;
    }

    public String getCuidador() {
        return cuidador;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    // Setters (si necesitas modificar los atributos)
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setCuidador(String cuidador) {
        this.cuidador = cuidador;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
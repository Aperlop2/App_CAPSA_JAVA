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

    public String getDetalle() {
        return detalle;
    }

    public String getCuidador() {
        return cuidador;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}

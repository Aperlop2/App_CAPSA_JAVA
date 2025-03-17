package com.example.java_capsa;

public class Cita {
    private String fecha;
    private String hora;
    private String ubicacion;

    public Cita(String fecha, String hora, String ubicacion) {
        this.fecha = fecha;
        this.hora = hora;
        this.ubicacion = ubicacion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}

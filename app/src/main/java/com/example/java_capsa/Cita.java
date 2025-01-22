package com.example.java_capsa;

public class Cita {
    private String id;
    private String fecha;
    private String hora;
    private String ubicacion;
    private String estado;

    public Cita(String id, String fecha, String hora, String ubicacion, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.ubicacion = ubicacion;
        this.estado = (estado != null) ? estado : "Pendiente"; // Si no tiene estado, lo marcamos como "Pendiente"
    }

    public String getId() {
        return id;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

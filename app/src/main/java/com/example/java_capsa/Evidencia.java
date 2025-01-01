package com.example.java_capsa;

public class Evidencia {
    private String detalle;
    private int rating;
    private String comentario;
    private int imagen;

    public Evidencia(String detalle, int rating, String comentario, int imagen) {
        this.detalle = detalle;
        this.rating = rating;
        this.comentario = comentario;
        this.imagen = imagen;
    }

    public String getDetalle() {
        return detalle;
    }

    public int getRating() {
        return rating;
    }

    public String getComentario() {
        return comentario;
    }

    public int getImagen() {
        return imagen;
    }
}

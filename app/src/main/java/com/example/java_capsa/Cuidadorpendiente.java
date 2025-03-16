package com.example.java_capsa;

public class Cuidadorpendiente {
    private String nombre;
    private String correo;
    private String telefono;
    private String especialidad;
    private String fotoPerfil ;

    // Constructor vacío para Firebase
    public Cuidadorpendiente() {}

    public Cuidadorpendiente( String nombre, String correo, String telefono, String especialidad, String fotoPerfil) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.fotoPerfil  = fotoPerfil ;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void getCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getfotoPerfil () {
        return fotoPerfil ;
    }

    public void setfotoPerfil (String fotoPerfil) {
        this.fotoPerfil  = fotoPerfil ;
    }
}

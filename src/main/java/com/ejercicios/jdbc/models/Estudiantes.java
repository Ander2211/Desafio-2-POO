package com.ejercicios.jdbc.models;

public class Estudiantes {
    private int id;
    private String nombreCompleto;
    private int edad;
    private String direccion;
    private String telefono;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Estudiantes(int id, String nombreCompleto, int edad, String direccion, String telefono) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.direccion = direccion;
        this.telefono = telefono;
    }
}

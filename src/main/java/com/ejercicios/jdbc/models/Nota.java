package com.ejercicios.jdbc.models;

public class Nota {
    private int id;
    private int estudianteId;
    private String grado;
    private String materia;
    private double notaFinal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public Nota(int id, int estudianteId, String grado, String materia, double notaFinal) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.grado = grado;
        this.materia = materia;
        this.notaFinal = notaFinal;
    }
}


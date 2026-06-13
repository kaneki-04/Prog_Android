package com.ejemplo.gestorpeliculas;
// Pelicula.java - Clase POJO para películas

public class Pelicula {
    private int idPelicula;
    private int idUsuario;
    private String titulo;
    private String director;
    private String genero;
    private int año;
    private String descripcion;
    private int puntuacion;
    private String estado;
    private String fechaAgregada;

    public Pelicula() {
    }

    public Pelicula(String titulo, String director, String genero, int año, 
                    String descripcion, String estado, int puntuacion) {
        this.titulo = titulo;
        this.director = director;
        this.genero = genero;
        this.año = año;
        this.descripcion = descripcion;
        this.estado = estado;
        this.puntuacion = puntuacion;
    }

    // Getters y Setters
    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaAgregada() {
        return fechaAgregada;
    }

    public void setFechaAgregada(String fechaAgregada) {
        this.fechaAgregada = fechaAgregada;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "titulo='" + titulo + '\'' +
                ", director='" + director + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}

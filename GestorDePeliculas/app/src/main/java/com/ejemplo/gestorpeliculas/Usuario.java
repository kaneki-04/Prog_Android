package com.ejemplo.gestorpeliculas;

// Usuario.java - Clase POJO para usuarios

public class Usuario {
    private int idUsuario;
    private String email;
    private String nombre;
    private String token;
    private String googleId;
    private String fechaRegistro;

    public Usuario() {
    }

    public Usuario(String email, String nombre, String token) {
        this.email = email;
        this.nombre = nombre;
        this.token = token;
    }

    public Usuario(int idUsuario, String email, String nombre, String token) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.token = token;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

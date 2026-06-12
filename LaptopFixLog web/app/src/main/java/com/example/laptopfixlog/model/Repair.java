package com.example.laptopfixlog.model;

import java.io.Serializable;

public class Repair implements Serializable {
    private String id;
    private String nombreCliente;
    private String modeloLaptop;
    private String tipoReparacion;
    private double costo;
    private String fecha;
    private String estado;
    private String comentario;

    public Repair(String id, String nombreCliente, String modeloLaptop, String tipoReparacion, double costo, String fecha, String estado, String comentario) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.modeloLaptop = modeloLaptop;
        this.tipoReparacion = tipoReparacion;
        this.costo = costo;
        this.fecha = fecha;
        this.estado = estado;
        this.comentario = comentario;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getModeloLaptop() { return modeloLaptop; }
    public void setModeloLaptop(String modeloLaptop) { this.modeloLaptop = modeloLaptop; }

    public String getTipoReparacion() { return tipoReparacion; }
    public void setTipoReparacion(String tipoReparacion) { this.tipoReparacion = tipoReparacion; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String toTxtLine() {
        String c = (comentario == null || comentario.isEmpty()) ? " " : comentario.replace("\n", " ");
        return id + "|" + nombreCliente + "|" + modeloLaptop + "|" + tipoReparacion + "|" + costo + "|" + fecha + "|" + estado + "|" + c;
    }
}
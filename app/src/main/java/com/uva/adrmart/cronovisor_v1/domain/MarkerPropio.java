package com.uva.adrmart.cronovisor_v1.domain;

import java.io.Serializable;

/**
 * Created by Adrian on 13/04/2016.
 */
public class MarkerPropio implements Serializable {

    private int id;
    private double latitud;
    private double logitud;
    private String titulo;
    private String descripcion;
    private int numImages;

    public MarkerPropio(){

    }

    public MarkerPropio(String descripcion, int id, double latitud, double logitud, String titulo ,int numImages) {
        this.descripcion = descripcion;
        this.id = id;
        this.latitud = latitud;
        this.logitud = logitud;
        this.titulo = titulo;
        this.numImages = numImages;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLogitud() {
        return logitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getNumImages() {
        return numImages;
    }

}


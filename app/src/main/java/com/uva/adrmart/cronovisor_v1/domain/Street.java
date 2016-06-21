package com.uva.adrmart.cronovisor_v1.domain;

import java.io.Serializable;

/**
 * Created by Adrian on 25/04/2016.
 */
public class Street implements Serializable {

    private int id;
    private String nombre;
    private String descripcion;
    private String representativo;
    private double lat;
    private double lon;

    public Street() {
    }

    public Street(String descripcion, int id, double lat, double lon, String nombre, String representativo) {
        this.descripcion = descripcion;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.nombre = nombre;
        this.representativo = representativo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getId() {
        return id;
    }

    public String getRepresentativo() {
        return representativo;
    }
}

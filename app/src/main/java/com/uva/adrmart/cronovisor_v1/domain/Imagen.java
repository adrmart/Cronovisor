package com.uva.adrmart.cronovisor_v1.domain;

import java.io.Serializable;

/**
 * Created by Adrian on 07/03/2016.
 */
public class Imagen implements Serializable {

    private int id;
    private int orientacion;
    private String url; //URL
    private String titulo;
    private String descripcion;
    private int año;
    private String autor;

    private String marker;
    private String street;

    public Imagen(){

    }

    public Imagen(String autor, int año, String street, String descripcion, int id, String url,
                  String marker, int orientacion, String titulo) {
        this.autor = autor;
        this.año = año;
        this.street = street;
        this.descripcion = descripcion;
        this.id = id;
        this.url = url;
        this.marker = marker;
        this.orientacion = orientacion;
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public String getAutor() {
        return autor;
    }

    public int getAño() {
        return año;
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

    public String getTitulo() {
        return titulo;
    }



}

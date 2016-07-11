package com.uva.adrmart.cronovisor_v1.domain;

import java.io.Serializable;

/**
 * Created by Adrian on 07/03/2016.
 */
public class Image implements Serializable {

    private int id;
    private String url; //URL
    private String titulo;
    private String descripcion;
    private int año;
    private String autor;

    private String marker;
    private String street;

/*
    private Bitmap image_bitmap;
*/

    public Image(){

    }

    public Image(String autor, int año, String street, String descripcion, int id, String url,
                 String marker, String titulo) {
        this.autor = autor;
        this.año = año;
        this.street = street;
        this.descripcion = descripcion;
        this.id = id;
        this.url = url;
        this.marker = marker;
        this.titulo = titulo;
    }

   /* public Image(String autor, int año, String street, String descripcion, int id, Bitmap image_bitmap,
                 String marker, String titulo) {
        this.autor = autor;
        this.año = año;
        this.street = street;
        this.descripcion = descripcion;
        this.id = id;
        this.image_bitmap = image_bitmap;
        this.marker = marker;
        this.titulo = titulo;
    }
*/
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

    public String getMarker() {
        return marker;
    }

    public String getStreet() {
        return street;
    }

  /*  public void setImage_bitmap(Bitmap image_bitmap) {
        this.image_bitmap = image_bitmap;
    }

    public Bitmap getImage_bitmap() {
        return image_bitmap;
    }*/
}

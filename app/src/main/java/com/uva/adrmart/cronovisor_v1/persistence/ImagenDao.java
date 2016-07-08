package com.uva.adrmart.cronovisor_v1.persistence;

import com.uva.adrmart.tfg.domain.Imagen;

import java.util.List;

/**
 * Created by Adrian on 13/04/2016.
 */
public interface ImagenDao {

    void findImagenLatLon(int latitud, int longitud);

    void findImagenes();

    List<Imagen> getListaImagenes();

    void findImagenesByStreet(int id);
}

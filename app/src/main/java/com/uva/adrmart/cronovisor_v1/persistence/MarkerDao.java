package com.uva.adrmart.cronovisor_v1.persistence;

import com.uva.adrmart.tfg.domain.MarkerPropio;

import java.util.List;

/**
 * Created by Adrian on 13/04/2016.
 */
public interface MarkerDao {

    void getMarkers();

    List <MarkerPropio> getListaMarkers();
}

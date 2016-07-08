package com.uva.adrmart.cronovisor_v1.persistence;

import com.uva.adrmart.tfg.domain.Street;

import java.util.List;

/**
 * Created by Adrian on 10/05/2016.
 */
public interface StreetDao {

    void findStreets();

    List<Street> getListaCalles();
}

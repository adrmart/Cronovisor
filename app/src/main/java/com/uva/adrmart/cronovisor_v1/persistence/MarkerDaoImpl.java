package com.uva.adrmart.cronovisor_v1.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uva.adrmart.tfg.BDHelper;
import com.uva.adrmart.tfg.domain.MarkerPropio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 13/04/2016.
 */
public class MarkerDaoImpl implements MarkerDao {

    //Tabla objetivo
    private static final String MARKER = "Marker";

    //Consultas sql
    private static final String FINDQUERY = "SELECT * FROM MarkerPropio;";

    private static final String ID_MARKER = "_id";
    private static final String LATITUD_MARKER = "latitud";
    private static final String LONGITUD_MARKER = "longitud";
    private static final String TITULO_MARKER = "titulo_marker";
    private static final String DESCRIPCION_MARKER = "descripcion_marker";

    //Variables sql
    private final BDHelper db;

    private static List<MarkerPropio> listaMarkerPropios;

    public MarkerDaoImpl(){
        db = BDHelper.getInstance();
        listaMarkerPropios = new ArrayList<>();
    }
    public List<MarkerPropio> getListaMarkers() {
        return listaMarkerPropios;
    }

    public static void setListaMarkerPropios(List<MarkerPropio> listaMarkerPropios) {
        MarkerDaoImpl.listaMarkerPropios = listaMarkerPropios;
    }

    @Override
    public void getMarkers() {
        SQLiteDatabase sqldb = db.getReadableDatabase();
        Cursor c = sqldb.rawQuery(FINDQUERY, null);
        Log.d(MARKER, "Marcadores devueltos: " + String.valueOf(c.getCount()));
        if (c.moveToFirst()){
            do{
                /*MarkerPropio m = new MarkerPropio(c.getString(c.getColumnIndex(DESCRIPCION_MARKER)),
                        c.getInt(c.getColumnIndex(ID_MARKER)),
                        c.getFloat(c.getColumnIndex(LATITUD_MARKER)),
                        c.getFloat(c.getColumnIndex(LONGITUD_MARKER)),
                        c.getString(c.getColumnIndex(TITULO_MARKER)));
                listaMarkerPropios.add(m);*/
            }while(c.moveToNext());
            c.close();
        }
    }
}

package com.uva.adrmart.cronovisor_v1.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uva.adrmart.tfg.BDHelper;
import com.uva.adrmart.tfg.domain.Street;
import com.uva.adrmart.tfg.domain.Imagen;
import com.uva.adrmart.tfg.domain.MarkerPropio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 13/04/2016.
 */
public class ImagenDaoImpl implements ImagenDao {

    //Tabla objetivo
    private static final String IMAGEN = "Imagen";

    //Consultas sql
    private static final String FINDQUERY = "SELECT * FROM Imagen;";
    private static final String FINDQUERYSTREET = "SELECT * FROM Imagen WHERE id_calle = ?;";

    //Campos de la tabla Imagen
    private static final String ID_IMAGEN = "_id";
    private static final String ORIENTACION = "orientacion";
    private static final String URL = "url";
    private static final String TITULO_IMAGEN = "titulo_imagen";
    private static final String DESCRIPCION_IMAGEN = "descripcion_imagen";
    private static final String AÑO = "año";
    private static final String AUTOR = "autor";

    //Variables sql
    private final BDHelper db;
    private static SQLiteDatabase sqldb;
    private static Cursor c;

    private static List<Imagen> listaImagenes;

    public ImagenDaoImpl() {
        db = BDHelper.getInstance();
        listaImagenes = new ArrayList<>();
    }

    @Override
    public void findImagenLatLon(int latitud, int longitud) {

    }

    @Override
    public List<Imagen> getListaImagenes() {
        return listaImagenes;
    }


    @Override
    public void findImagenes() {
        sqldb = db.getReadableDatabase();
        c = sqldb.rawQuery(FINDQUERY, null);
        Log.d(IMAGEN, "Imagenes devueltas: " + String.valueOf(c.getCount()));
        if (c.moveToFirst()){
            do{
                Street street = new Street();
                MarkerPropio marker = new MarkerPropio();
               /* Imagen i = new Imagen(c.getString(c.getColumnIndex(AUTOR)),
                        c.getInt(c.getColumnIndex(AÑO)),
                        street,
                        c.getString(c.getColumnIndex(DESCRIPCION_IMAGEN)),
                        c.getInt(c.getColumnIndex(ID_IMAGEN)),
                        c.getInt(c.getColumnIndex(URL)),
                        marker,
                        c.getInt(c.getColumnIndex(ORIENTACION)),
                        c.getString(c.getColumnIndex(TITULO_IMAGEN)));*/
                Imagen i = new Imagen();
                listaImagenes.add(i);

            }while(c.moveToNext());
        }
    }

    @Override
    public void findImagenesByStreet (int id) {
        sqldb = db.getReadableDatabase();
        c = sqldb.rawQuery(FINDQUERYSTREET,new String[]{Integer.toString(id)});
        Log.d(IMAGEN, "Imagenes devueltas para id "+ id + " :" + String.valueOf(c.getCount()));

        if (c.moveToFirst()){
            do{
                Street street = new Street();
                MarkerPropio marker = new MarkerPropio();
                /*Imagen i = new Imagen(c.getString(c.getColumnIndex(AUTOR)),
                        c.getInt(c.getColumnIndex(AÑO)),
                        street,
                        c.getString(c.getColumnIndex(DESCRIPCION_IMAGEN)),
                        c.getInt(c.getColumnIndex(ID_IMAGEN)),
                        c.getInt(c.getColumnIndex(URL)),
                        marker,
                        c.getInt(c.getColumnIndex(ORIENTACION)),
                        c.getString(c.getColumnIndex(TITULO_IMAGEN)));*/
                Imagen i = new Imagen();
                listaImagenes.add(i);

            } while(c.moveToNext());
        }

    }
}

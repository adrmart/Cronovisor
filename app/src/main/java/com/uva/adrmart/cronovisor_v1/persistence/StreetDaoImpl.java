package com.uva.adrmart.cronovisor_v1.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uva.adrmart.tfg.BDHelper;
import com.uva.adrmart.tfg.domain.Street;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 10/05/2016.
 */
public class StreetDaoImpl implements StreetDao{

    //Tabla objetivo
    private static final String CALLE = "Street";

    //Consultas sql
    private static final String FINDQUERY = "SELECT * FROM Street;";
    private static final String FINDNUMIMAGES = "SELECT _id FROM Imagen i WHERE id_calle = ?" ;

    //Campos de la tabla Imagen
    private static final String ID_CALLE = "_id";
    private static final String TIPO_CALLE = "tipo_calle";
    private static final String NOMBRE_CALLE = "nombre_calle";
    private static final String DESCRIPCION_CALLE = "descripcion_calle";
    public static final String REPRESENTATIVO = "representativo";

    //Variables sql
    private final BDHelper db;
    private static SQLiteDatabase sqldb;
    private static Cursor c;

    private static List<Street> listaStreets;

    public StreetDaoImpl() {
        db = BDHelper.getInstance();
        listaStreets = new ArrayList<>();
    }

    @Override
    public void findStreets() {
        sqldb = db.getReadableDatabase();
        c = sqldb.rawQuery(FINDQUERY, null);
        Log.d(CALLE, "Calles devueltas devueltas: " + String.valueOf(c.getCount()));
        if (c.moveToFirst()){
            do{
                /*Imagen imagen = new Imagen();
                Street street = new Street(c.getString(c.getColumnIndex(DESCRIPCION_CALLE)),
                        c.getInt(c.getColumnIndex(ID_CALLE)),
                        c.getString(c.getColumnIndex(NOMBRE_CALLE)),
                        c.getString(c.getColumnIndex(TIPO_CALLE)),
                        "");
                street.setNumImages(getNumImages( c.getInt(c.getColumnIndex(ID_CALLE))));
                listaStreets.add(street);*/

            }while(c.moveToNext());
        }
    }

    private int getNumImages(int id) {
        sqldb = db.getReadableDatabase();
        c =  sqldb.rawQuery(FINDNUMIMAGES, new String[]{Integer.toString(id)});

        int count = 0;
        if (c.moveToFirst()){
            do{
                count = c.getCount();
            }while(c.moveToNext());
        }
        return count;
    }

    @Override
    public List<Street> getListaCalles() {
        return listaStreets;
    }
}

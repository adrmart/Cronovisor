package com.uva.adrmart.cronovisor_v1.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uva.adrmart.cronovisor_v1.database.DBHelper;

import java.util.HashMap;

/**
 * Created by Adrian on 25/06/2016.
 */
public class NotificationDaoImpl implements NotificationDao{
    private static final String ID = "_id";
    private static final String ID_STREET = "id_street";
    private static final String TAG = NotificationDaoImpl.class.getName();

    //Variables sql
    private final DBHelper db;
    private static final String FINDQUERY = "SELECT * FROM Notification;";
    private SQLiteDatabase sqldb;

    public NotificationDaoImpl(){
        db = DBHelper.getInstance();
        Log.d(TAG, "db: " + db);
    }

    @Override
    public HashMap<Integer, Integer> getNotifications() {
        Log.d(TAG, "db: " + db);

        sqldb = db.getReadableDatabase();
        Cursor c = sqldb.rawQuery(FINDQUERY, null);
        HashMap<Integer,Integer> listaNotificaciones = new HashMap<>();
        if (c.moveToFirst()) {
            do{
                listaNotificaciones.put(c.getInt(c.getColumnIndex(ID_STREET)), c.getInt(c.getColumnIndex(ID)));
            }while(c.moveToNext());
        }
        return listaNotificaciones;
    }

    @Override
    public void addNotification(int id) {
        sqldb = db.getWritableDatabase();
        //Valores para la busqueda en la base de datos
        ContentValues values = new ContentValues();
        values.put(ID_STREET, id);
        try{
            sqldb.insert("Notification", null, values);
        } catch (Exception e) {
            Log.d(TAG, "error al escribir");
        }
    }
}

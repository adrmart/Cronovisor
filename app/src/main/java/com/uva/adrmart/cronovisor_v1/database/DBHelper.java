package com.uva.adrmart.cronovisor_v1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Adrian on 13/04/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getName();
    private static final String DATABASE_NAME = "CRONOVISOR.db";
    private static final int DATABASE_VERSION = 1;
    private static DBHelper DBHelper;

    private DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public static void init(Context context) {
        Log.d("BD" , "init");
        DBHelper = new DBHelper(context);
    }

    public static DBHelper getInstance() {
        return DBHelper;
    }

    //- Crea la BDD
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate");

        //- Limpiar BD
        db.execSQL(ScriptBD.DROP_CALLE);
        db.execSQL(ScriptBD.DROP_MARKER);
        db.execSQL(ScriptBD.DROP_IMAGEN);
        db.execSQL(ScriptBD.DROP_NOTIFICATION);

        //- Crear BD
        db.execSQL(ScriptBD.CALLE_SCRIPT);
        db.execSQL(ScriptBD.MARKER_SCRIPT);
        db.execSQL(ScriptBD.IMAGEN_SCRIPT);
        db.execSQL(ScriptBD.NOTIFICATION_SCRIPT);
    }

    //- Actualiza la BDD
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "onUpgrade");
        onCreate(db);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}

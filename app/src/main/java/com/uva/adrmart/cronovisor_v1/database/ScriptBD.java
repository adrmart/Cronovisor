package com.uva.adrmart.cronovisor_v1.database;

import android.provider.BaseColumns;

/**
 * Clase que contiene los scripts necesarios para la creacion de la base de datos
 * de la aplicación. Contiene valor por defecto para las tablas
 */
class ScriptBD {

    //- Tablas de la BD
    private static final String IMAGEN = "Imagen";
    private static final String MARKER = "MarkerPropio";
    private static final String CALLE = "Street";
    private static final String NOTIFICATION = "Notification";

    //- Campos tabla IMAGEN
    private static final String ID_IMAGEN = BaseColumns._ID;
    private static final String IMAGE = "imagen";
    private static final String TITULO_IMAGEN = "titulo_imagen";
    private static final String DESCRIPCION_IMAGEN = "descripcion_imagen";
    private static final String AÑO = "año";
    private static final String AUTOR = "autor";
    private static final String ID_MARKER_FOR = "id_marker";
    private static final String ID_CALLE_FOR = "id_calle";

    //- Campos tabla MARKER
    private static final String ID_MARKER = BaseColumns._ID;
    private static final String LATITUD_MARKER = "latitud";
    private static final String LONGITUD_MARKER = "longitud";
    private static final String TITULO_MARKER = "titulo_marker";
    private static final String DESCRIPCION_MARKER = "descripcion_marker";
    private static final String NUM_IMAGES_MARKER = "num_images";

    //- Campos tabla CALLE
    private static final String ID_CALLE = BaseColumns._ID;
    private static final String NOMBRE_CALLE = "nombre_calle";
    private static final String DESCRIPCION_CALLE = "descripcion_calle";
    private static final String REPRESENTATIVO = "representativo";

    //- Campos tabla NOTIFICATION
    private static final String ID_NOTIFICACIÖN = BaseColumns._ID;
    private static final String ID_STREET = "id_street";

    //- Creacion tabla IMAGEN
    public static final String IMAGEN_SCRIPT =
            "create table " + IMAGEN + "(" +
                    ID_IMAGEN + " integer primary key," +
                    ID_CALLE_FOR + " integer," +
                    ID_MARKER_FOR + " integer," +
                    IMAGE + " blob not null," +
                    TITULO_IMAGEN + " text not null," +
                    DESCRIPCION_IMAGEN + " text not null," +
                    AÑO + " integer," +
                    AUTOR + " text)";

    //- Creacion tabla MARKER
    public static final String MARKER_SCRIPT =
            "create table " + MARKER + "(" +
                    ID_MARKER + " integer primary key," +
                    LATITUD_MARKER + " float,"+
                    LONGITUD_MARKER + " float,"+
                    TITULO_MARKER + " text not null,"+
                    DESCRIPCION_MARKER + " text not null, " +
                    NUM_IMAGES_MARKER + " int)";

    //- Creacion tabla CALLE
    public static final String CALLE_SCRIPT =
            "create table " + CALLE + "(" +
                    ID_CALLE + " integer primary key," +
                    NOMBRE_CALLE + " text not null," +
                    DESCRIPCION_CALLE + " text not null," +
                    REPRESENTATIVO + " blob not null)";

    //- Creación tabla NOTIFICATION
    public static final String NOTIFICATION_SCRIPT =
            "create table " + NOTIFICATION + "(" +
                    ID_NOTIFICACIÖN + " integer primary key autoincrement," +
                    ID_STREET + " id not null)";

    //- Eliminacion tabla IMAGEN
    public static final String DROP_IMAGEN =
            "DROP TABLE IF EXISTS " + IMAGEN;

    //- Eliminacion tabla MARKER
    public static final String DROP_MARKER =
            "DROP TABLE IF EXISTS " + MARKER;

    //- Eliminacion tabla CALLE
    public static final String DROP_CALLE =
            "DROP TABLE IF EXISTS " + CALLE;

    //- Eliminacion tabla NOTIFICACION
    public static final String DROP_NOTIFICATION =
            "DROP TABLE IF EXISTS " + NOTIFICATION;

}

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

    //- Campos tabla IMAGEN
    private static final String ID_IMAGEN = BaseColumns._ID;
    private static final String ORIENTACION = "orientacion";
    private static final String URL = "url";
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

    //- Campos tabla CALLE
    private static final String ID_CALLE = BaseColumns._ID;
    private static final String NOMBRE_CALLE = "nombre_calle";
    private static final String TIPO_CALLE = "tipo_calle";
    private static final String DESCRIPCION_CALLE = "descripcion_calle";
    private static final String REPRESENTATIVO = "representativo";

    //- Creacion tabla IMAGEN
    public static final String IMAGEN_SCRIPT =
            "create table " + IMAGEN + "(" +
                    ID_IMAGEN + " integer primary key autoincrement," +
                    ID_CALLE_FOR + " integer," +
                    ID_MARKER_FOR + " integer," +
                    ORIENTACION + " integer," +
                    URL + " integer not null," +
                    TITULO_IMAGEN + " text not null," +
                    DESCRIPCION_IMAGEN + " text not null," +
                    AÑO + " integer," +
                    AUTOR + " text)";

    //- Creacion tabla MARKER
    public static final String MARKER_SCRIPT =
            "create table " + MARKER + "(" +
                    ID_MARKER + " integer primary key autoincrement," +
                    LATITUD_MARKER + " float,"+
                    LONGITUD_MARKER + " float,"+
                    TITULO_MARKER + " text not null,"+
                    DESCRIPCION_MARKER + " text not null)";

    //- Creacion tabla CALLE
    public static final String CALLE_SCRIPT =
            "create table " + CALLE + "(" +
                    ID_CALLE + " integer primary key autoincrement," +
                    NOMBRE_CALLE + " text not null," +
                    DESCRIPCION_CALLE + " text not null," +
                    TIPO_CALLE + " text not null," +
                    REPRESENTATIVO + " id not null)";

    //- Eliminacion tabla IMAGEN
    public static final String DROP_IMAGEN =
            "DROP TABLE IF EXISTS " + IMAGEN;

    //- Eliminacion tabla MARKER
    public static final String DROP_MARKER =
            "DROP TABLE IF EXISTS " + MARKER;

    //- Eliminacion tabla CALLE
    public static final String DROP_CALLE =
            "DROP TABLE IF EXISTS " + CALLE;

    //- Inserccion de datos de calles
    public static final String INSERT_CALLE1_SCRIPT =
            "insert into " + CALLE + " values (" +
                    "1," +
                    "\"Colon\"," +
                    "\"Plaza de colon remodelada en el año....\"," +
                    "\"Plaza\"," +
                    "1)";

    public static final String INSERT_CALLE2_SCRIPT =
            "insert into " + CALLE + " values (" +
                    "2," +
                    "\"Recoletos\"," +
                    "\"Paseo de recoletos remodelada en el año....\"," +
                    "\"Paseo\"," +
                    "2)";

    //- Inserccion de datos de marcadores
    public static final String INSERT_MARKER1_SCRIPT =
            "insert into " + MARKER + " values (" +
                    "1," +
                    "41.644705,"+
                    "-4.727278,"+
                    "\"Plaza colon 1\"," +
                    "\"Vista lateral de la estatua y el hospital\")";

    public static final String INSERT_MARKER2_SCRIPT =
            "insert into " + MARKER + " values (" +
                    "2," +
                    "41.644723,"+
                    "-4.72762,"+
                    "\"Plaza colon 2\"," +
                    "\"Vista frontal de la estatua\")";

    public static final String INSERT_MARKER3_SCRIPT =
            "insert into " + MARKER + " values (" +
                    "3," +
                    "41.644387,"+
                    "-4.727143,"+
                    "\"Plaza colon 3\"," +
                    "\"Antiguos depositos\")";

    public static final String INSERT_MARKER4_SCRIPT =
            "insert into " + MARKER + " values (" +
                    "4," +
                    "41.644098,"+
                    "-4.727328,"+
                    "\"Plaza colon 4\"," +
                    "\"Vista trasera de la estatua y edificos de acera recoletos\")";


    //-  Inserccion de datos de imagenes
    public static final String INSERT_IMAGEN1_SCRIPT =
            "insert into " + IMAGEN + " values (" +
                    "1," +
                    "1," +
                    "1," +
                    "263,"+
                    "\"R.drawable.colon3\"," +
                    "\"Plaza colon 3\"," +
                    "\"Vista lateral de la plaza colon y hospital\","+
                    "2000,"+
                    "\"Desconocido\")";

    public static final String INSERT_IMAGEN2_SCRIPT =
            "insert into " + IMAGEN + " values (" +
                    "2," +
                    "1," +
                    "2," +
                    "98,"+
                    "\"R.drawable.colon1\"," +
                    "\"Plaza colon 1\"," +
                    "\"Vista lateral de la plaza colon y hospital\","+
                    "2000,"+
                    "\"Desconocido\")";

    public static final String INSERT_IMAGEN3_SCRIPT =
            "insert into " + IMAGEN + " values (" +
                    "3," +
                    "1," +
                    "2," +
                    "98,"+
                    "\"R.drawable.colon5\"," +
                    "\"Plaza colon 5\"," +
                    "\"Vista lateral de la plaza colon y hospital\","+
                    "2000,"+
                    "\"Desconocido\")";

    public static final String INSERT_IMAGEN4_SCRIPT =
            "insert into " + IMAGEN + " values (" +
                    "4," +
                    "1," +
                    "1," +
                    "97,"+
                    "\"R.drawable.colon4\"," +
                    "\"Plaza colon4\"," +
                    "\"Vista lateral de la plaza colon y hospital\","+
                    "2000,"+
                    "\"Desconocido\")";

    public static final String INSERT_IMAGEN5_SCRIPT =
            "insert into " + IMAGEN + " values (" +
                    "5," +
                    "1," +
                    "3," +
                    "326,"+
                    "\"R.drawable.colon6\"," +
                    "\"Plaza colon 6\"," +
                    "\"Vista lateral de la plaza colon y hospital\","+
                    "2000,"+
                    "\"Desconocido\")";

    public static final String INSERT_IMAGEN6_SCRIPT =
            "insert into " + IMAGEN + " values (" +
                    "6," +
                    "1," +
                    "4," +
                    "97,"+
                    "\"R.drawable.colon7\"," +
                    "\"Plaza colon 7\"," +
                    "\"Vista lateral de la plaza colon y hospital\","+
                    "2000,"+
                    "\"Desconocido\")";

    public static final String INSERT_IMAGEN7_SCRIPT =
            "insert into " + IMAGEN + " values (" +
                    "7," +
                    "2," +
                    "4," +
                    "97,"+
                    "\"R.drawable.colon7\"," +
                    "\"Plaza colon 7\"," +
                    "\"Vista lateral de la plaza colon y hospital\","+
                    "2000,"+
                    "\"Desconocido\")";

}

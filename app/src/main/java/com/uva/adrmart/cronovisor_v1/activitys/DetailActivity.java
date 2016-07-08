package com.uva.adrmart.cronovisor_v1.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.domain.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/** Actividad que descarga y muestra una imagen al usuario
 *  Dicha imagen es ampliable para una mejor visualización
 *
 * Created by Adrian on 27/04/2016.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getName();

    public static final String EXTRA_PARAM_ID = "com.uva.adrmart.tfg.ID";

    private static final String URL_IMAGE = "http://virtual.lab.inf.uva.es:20202/image/";
    private static final String URL_JSON = "?format=json";

    private ImageView imagenDetalle;
    private TextView texto;

    private Image image;
    private RequestQueue requestQueue;
    private boolean isClicked;
    private ImageViewTouch imagenExpand;
    private String idioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idioma = Locale.getDefault().getDisplayLanguage();
        requestQueue= Volley.newRequestQueue(this);
        doubleLayout();
    }

    private void doubleLayout(){
        isClicked = false;
        setContentView(R.layout.activity_detalle);
        imagenDetalle = (ImageView) findViewById(R.id.imagen_pequeña);
        texto = (TextView) findViewById(R.id.text_image);
        if (image ==null){
            getImage(getIntent().getExtras().getInt(EXTRA_PARAM_ID));
        } else {
            cargarImagen();
        }
        imagenDetalle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                expandLayout();
            }
        });

    }

    private void expandLayout(){
        setContentView(R.layout.expand);
        imagenExpand = (ImageViewTouch) findViewById(R.id.imagen_grande);
        isClicked = true;
        cargaImagenExtendida();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isClicked){
            doubleLayout();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void getImage(int id){
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(URL_IMAGE+id+URL_JSON, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                // Obtener el marker del objeto
                if (idioma.equals("español")){
                    try {
                        image = new Image(response.getString("autor"),
                                response.getInt("year"),
                                response.getString("id_street"),
                                response.getString("description_es"),
                                response.getInt("id"),
                                response.getString("image"),
                                response.getString("id_marker"),
                                response.getInt("orientation"),
                                response.getString("title_es"));
                        cargarImagen();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error de parsing: "+ e.getMessage() + "/// "+ e.getCause());
                        Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();

                    }
                } else{
                    try {
                        image = new Image(response.getString("autor"),
                                response.getInt("year"),
                                response.getString("id_street"),
                                response.getString("description_en"),
                                response.getInt("id"),
                                response.getString("image"),
                                response.getString("id_marker"),
                                response.getInt("orientation"),
                                response.getString("title_en"));
                        cargarImagen();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error de parsing: "+ e.getMessage() + "/// "+ e.getCause());
                        Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();

                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
                Toast.makeText(getBaseContext(), getText(R.string.server_fail), Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(jsArrayRequest);
    }

    private void cargarImagen() {
        Glide.with(imagenDetalle.getContext())
                    .load(image.getUrl())
                    .into(imagenDetalle);
        String text = image.getTitulo() + "\r\n" +
                getString(R.string.image_autor) + ": " + image.getAutor() + "\r\n" + getString(R.string.image_año) + ": " + image.getAño() + "\r\n"+
                image.getDescripcion();
        texto.setText(text);
    }
    private void cargaImagenExtendida(){
        Glide.with(imagenExpand.getContext())
                .load(image.getUrl())
                .into(imagenExpand);

    }
}

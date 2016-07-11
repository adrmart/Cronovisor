package com.uva.adrmart.cronovisor_v1.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * Shows an image to the user
 *
 * Created by Adrian on 27/04/2016.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getName();

    public static final String EXTRA_PARAM_ID = "com.uva.adrmart.tfg.ID";

    private static final String URL_IMAGE = "http://virtual.lab.inf.uva.es:20202/image/";
    private static final String URL_JSON = "?format=json";

    private ImageView imagenDetalle;
    private TextView text;
    private TextView title;
    private TextView autor;
    private TextView year;

    private Image image;
    private RequestQueue requestQueue;
    private boolean isClicked = false;
    private ImageViewTouch imagenExpand;
    private String idioma;
    private boolean textExpanded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idioma = Locale.getDefault().getDisplayLanguage();
        requestQueue= Volley.newRequestQueue(this);
        doubleLayout();
    }

    /**
     * start layout with text and image
     */
    private void doubleLayout(){
        isClicked = false;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detalle);
        imagenDetalle = (ImageView) findViewById(R.id.imagen_pequeña);
        text = (TextView) findViewById(R.id.text_image);
        title = (TextView) findViewById(R.id.title_image);
        autor = (TextView) findViewById(R.id.autor_text);
        year = (TextView) findViewById(R.id.year_text);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.text_layout);
        assert layout != null;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textExpanded){
                    layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, 0.6f));
                    textExpanded = false;
                } else{
                    layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, 0.3f));
                    textExpanded = true;
                }

            }
        });
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

    /**
     * Starts layout with only image
     */
    private void expandLayout(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.image_expand);
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

    /**
     * Request image to the server
     * @param id id of the image
     */
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

    /**
     * Put values in holder
     */
    private void cargarImagen() {
        Glide.with(imagenDetalle.getContext())
                    .load(image.getUrl())
                    .into(imagenDetalle);
        String text_autor = getString(R.string.image_autor) + ": " + image.getAutor();
        String text_year = getString(R.string.image_año) + ": " + image.getAño();

        title.setText(image.getTitulo());
        autor.setText(text_autor);
        year.setText(text_year);
        text.setText(image.getDescripcion());
    }

    /**
     * Put values in image
     */
    private void cargaImagenExtendida(){
        Glide.with(imagenExpand.getContext())
                .load(image.getUrl())
                .into(imagenExpand);

    }


}

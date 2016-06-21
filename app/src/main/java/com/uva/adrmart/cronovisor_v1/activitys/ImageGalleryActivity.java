package com.uva.adrmart.cronovisor_v1.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.adapter.GridViewImageAdapter;
import com.uva.adrmart.cronovisor_v1.domain.Imagen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Adrian on 02/06/2016.
 */
public class ImageGalleryActivity extends AppCompatActivity {

    private static final String TAG = StreetGalleryActivity.class.getName();
    public static final String EXTRA_PARAM = "com.uva.adrmart.tfg.ACTIVITY";
    public static final String EXTRA_PARAM_ID = "com.uva.adrmart.tfg.ID";
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    private ProgressBar mProgressBar;
    private GridViewImageAdapter mGridAdapter;
    private ArrayList<Imagen> mGridData;
    private RequestQueue requestQueue;

    public static final String URL_FROM_STREET= "http://virtual.lab.inf.uva.es:20202/imagesstreet/";
    public static final String URL_FROM_MARKER= "http://virtual.lab.inf.uva.es:20202/imagesmarker/";

    public static final String URL_FORMAT = "/?format=json";
    private String idioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idioma = Locale.getDefault().getDisplayLanguage();
        setContentView(R.layout.activity_gallery_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        GridView mGridView = (GridView) findViewById(R.id.gridViewImage);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewImageAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                Imagen item = (Imagen) parent.getItemAtPosition(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(ImageGalleryActivity.this, DetalleActivity.class);
                intent.putExtra(DetalleActivity.EXTRA_PARAM_ID, item.getId());

                //Start details activity
                startActivity(intent);
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        mProgressBar.setVisibility(View.VISIBLE);
        requestImages();
    }

    private void requestImages(){
        JsonArrayRequest jsArrayRequest;

        // 1 -> mapa  2 -> galleria
        if (getIntent().getExtras().getInt(EXTRA_PARAM)==1){
            jsArrayRequest = new JsonArrayRequest(URL_FROM_MARKER +
                    getIntent().getExtras().getInt(EXTRA_PARAM_ID) +
                    URL_FORMAT, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {

                    Log.d(TAG, String.valueOf(response));
                    Log.d("RESQUEST", String.valueOf(Thread.currentThread()));

                    // Obtener la imagen del objeto
                    for(int i=0; i<response.length(); i++){
                        try {
                            JSONObject objeto= response.getJSONObject(i);
                            Imagen imagen = new Imagen(objeto.getString("autor"),
                                    objeto.getInt("year"),
                                    objeto.getString("id_street"),
                                    objeto.getString("description"),
                                    objeto.getInt("id"),
                                    objeto.getString("image"),
                                    objeto.getString("id_marker"),
                                    objeto.getInt("orientation"),
                                    objeto.getString("title"));
                            mGridData.add(imagen);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error de parsing: "+ e.getMessage());
                        }
                        if (i==response.length()-1){
                            Log.d(TAG,"Ultimo elemento de la respuesta: " + i);
                        }
                    }
                    mGridAdapter.setGridData(mGridData);
                    mProgressBar.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
                }
            });
        } else{
            jsArrayRequest = new JsonArrayRequest(URL_FROM_STREET +
                    getIntent().getExtras().getInt(EXTRA_PARAM_ID) +
                    URL_FORMAT, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {

                    Log.d(TAG, String.valueOf(response));
                    Log.d("RESQUEST", String.valueOf(Thread.currentThread()));

                    // Obtener la imagen del objeto
                    if (idioma.equals("español")){
                        for(int i=0; i<response.length(); i++){
                            try {
                                JSONObject objeto= response.getJSONObject(i);
                                Imagen imagen = new Imagen(objeto.getString("autor"),
                                        objeto.getInt("year"),
                                        objeto.getString("id_street"),
                                        objeto.getString("description_es"),
                                        objeto.getInt("id"),
                                        objeto.getString("image"),
                                        objeto.getString("id_marker"),
                                        objeto.getInt("orientation"),
                                        objeto.getString("title_es"));
                                mGridData.add(imagen);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error de parsing: "+ e.getMessage());
                            }
                            if (i==response.length()-1){
                                Log.d(TAG,"Ultimo elemento de la respuesta: " + i);
                            }
                        }
                    } else {
                        for(int i=0; i<response.length(); i++){
                            try {
                                JSONObject objeto= response.getJSONObject(i);
                                Imagen imagen = new Imagen(objeto.getString("autor"),
                                        objeto.getInt("year"),
                                        objeto.getString("id_street"),
                                        objeto.getString("description_en"),
                                        objeto.getInt("id"),
                                        objeto.getString("image"),
                                        objeto.getString("id_marker"),
                                        objeto.getInt("orientation"),
                                        objeto.getString("title_en"));
                                mGridData.add(imagen);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error de parsing: "+ e.getMessage());
                            }
                            if (i==response.length()-1){
                                Log.d(TAG,"Ultimo elemento de la respuesta: " + i);
                            }
                        }
                    }

                    mGridAdapter.setGridData(mGridData);
                    mProgressBar.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
                }
            });
        }

        requestQueue.add(jsArrayRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search).setVisible(false);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onOptionsItemSelected(item);
    }
}

package com.uva.adrmart.cronovisor_v1.activitys;

import android.app.NotificationManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.adapter.GridViewImageAdapter;
import com.uva.adrmart.cronovisor_v1.domain.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adrian on 02/06/2016.
 */
public class GalleryImageActivity extends AppCompatActivity {

    private static final String TAG = GalleryStreetActivity.class.getName();
    public static final String EXTRA_PARAM = "com.uva.adrmart.tfg.ACTIVITY";
    public static final String EXTRA_PARAM_ID = "com.uva.adrmart.tfg.ID";
    public static final String EXTRA_PARAM_NAME_STREET = "com.uva.adrmart.tfg.NAMESTREET";
    public static final String EXTRA_PARAM_NAME_MARKER = "com.uva.adrmart.tfg.NAMEMARKER";

    private ProgressBar mProgressBar;
    private GridViewImageAdapter mGridAdapter;
    private ArrayList<Image> mGridData;
    private RequestQueue requestQueue;
    private String streetFather;
/*
    private ImagenDao imagenDao;
*/

    private static final String URL_FROM_STREET= "http://virtual.lab.inf.uva.es:20202/imagesstreet/";
    public static final String URL_FROM_MARKER= "http://virtual.lab.inf.uva.es:20202/imagesmarker/";

    public static final String URL_FORMAT = "/?format=json";
    private String idioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idioma = Locale.getDefault().getDisplayLanguage();
        setContentView(R.layout.activity_gallery_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*imagenDao = new ImagenDaoImpl();*/
        streetFather = null;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        GridView mGridView = (GridView) findViewById(R.id.gridViewImage);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarImage);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewImageAdapter(this, mGridData);
        assert mGridView != null;
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                Image item = (Image) parent.getItemAtPosition(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(GalleryImageActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_PARAM_ID, item.getId());

                //Start details activity
                startActivity(intent);
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        mProgressBar.setVisibility(View.VISIBLE);
        requestImages();
    }

    /**
     * Request images to show. Check where the intent comes to show diferente images
     */
    private void requestImages(){
        JsonArrayRequest jsArrayRequest;

        // 1 -> mapa  2 -> galleria  3 -> servicio
        if (getIntent().getExtras().getInt(EXTRA_PARAM)==1){
            setTitle(getIntent().getExtras().getString(EXTRA_PARAM_NAME_MARKER));
            jsArrayRequest = new JsonArrayRequest(URL_FROM_MARKER +
                    getIntent().getExtras().getInt(EXTRA_PARAM_ID) +
                    URL_FORMAT, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {

                    // Obtener la imagen del objeto
                    if (idioma.equals("español")){
                        for(int i=0; i<response.length(); i++){
                            try {
                                JSONObject objeto= response.getJSONObject(i);
                                Image image = new Image(objeto.getString("autor"),
                                        objeto.getInt("year"),
                                        objeto.getString("id_street"),
                                        objeto.getString("description_es"),
                                        objeto.getInt("id"),
                                        objeto.getString("image"),
                                        objeto.getString("id_marker"),
                                        objeto.getString("title_es"));
                                mGridData.add(image);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error de parsing: "+ e.getMessage());
                                Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        for(int i=0; i<response.length(); i++){
                            try {
                                JSONObject objeto= response.getJSONObject(i);
                                Image image = new Image(objeto.getString("autor"),
                                        objeto.getInt("year"),
                                        objeto.getString("id_street"),
                                        objeto.getString("description_en"),
                                        objeto.getInt("id"),
                                        objeto.getString("image"),
                                        objeto.getString("id_marker"),
                                        objeto.getString("title_en"));
                                mGridData.add(image);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error de parsing: "+ e.getMessage());
                                Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();
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
                    /*if (imagenDao.findImagenesByMarker(getIntent().getExtras().getInt(EXTRA_PARAM_ID))!=null){
                        mGridData = imagenDao.findImagenesByMarker(getIntent().getExtras().getInt(EXTRA_PARAM_ID));
                        mGridAdapter.setGridData(mGridData);
                        mProgressBar.setVisibility(View.GONE);
                    } else{
                    }*/
                    Toast.makeText(getBaseContext(), getText(R.string.server_fail), Toast.LENGTH_LONG).show();
                }
            });
        } else{
            Log.d(TAG, "NAME " + getIntent().getExtras().getString(EXTRA_PARAM_NAME_STREET));
            streetFather = getIntent().getExtras().getString(EXTRA_PARAM_NAME_STREET);
            if (streetFather!=null){
                setTitle(streetFather);
            }
            if (getIntent().getExtras().getInt(EXTRA_PARAM)==3){
                NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNM.cancel(1);
            }
            jsArrayRequest = new JsonArrayRequest(URL_FROM_STREET +
                    getIntent().getExtras().getInt(EXTRA_PARAM_ID) +
                    URL_FORMAT, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {

                    // Obtener la imagen del objeto
                    if (idioma.equals("español")){
                        for(int i=0; i<response.length(); i++){
                            try {
                                JSONObject objeto= response.getJSONObject(i);
                                Image image = new Image(objeto.getString("autor"),
                                        objeto.getInt("year"),
                                        objeto.getString("id_street"),
                                        objeto.getString("description_es"),
                                        objeto.getInt("id"),
                                        objeto.getString("image"),
                                        objeto.getString("id_marker"),
                                        objeto.getString("title_es"));
                                mGridData.add(image);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error de parsing: "+ e.getMessage());
                                Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        for(int i=0; i<response.length(); i++){
                            try {
                                JSONObject objeto= response.getJSONObject(i);
                                Image image = new Image(objeto.getString("autor"),
                                        objeto.getInt("year"),
                                        objeto.getString("id_street"),
                                        objeto.getString("description_en"),
                                        objeto.getInt("id"),
                                        objeto.getString("image"),
                                        objeto.getString("id_marker"),
                                        objeto.getString("title_en"));
                                mGridData.add(image);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error de parsing: "+ e.getMessage());
                                Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();
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
                   /* if (imagenDao.findImagenesByStreet(getIntent().getExtras().getInt(EXTRA_PARAM_ID))!=null){
                        mGridData = imagenDao.findImagenesByStreet(getIntent().getExtras().getInt(EXTRA_PARAM_ID));
                        mGridAdapter.setGridData(mGridData);
                        mProgressBar.setVisibility(View.GONE);
                    } else{
                        Toast.makeText(getBaseContext(), getText(R.string.server_fail), Toast.LENGTH_LONG).show();
                    }*/
                    Toast.makeText(getBaseContext(), getText(R.string.server_fail), Toast.LENGTH_LONG).show();
                }
            });
        }
        requestQueue.add(jsArrayRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_gallery; this adds items to the action bar if it is present.
        if (getIntent().getExtras().getInt(EXTRA_PARAM)!=1){
            getMenuInflater().inflate(R.menu.menu_gallery_image, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.view_on_map:
                Intent i = new Intent(this, MapsActivity.class);
                Geocoder geocoder = new Geocoder(this);
                if (streetFather!=null){
                    Log.d(TAG, "Street - >" + streetFather);
                    try {
                        List<Address> address = geocoder.getFromLocationName(streetFather,1);
                        Log.d(TAG, "LAT/lon" +address.get(0).getLatitude() + address.get(0).getLongitude());
                        i.putExtra(MapsActivity.EXTRA_PARAM_LAT, address.get(0).getLatitude());
                        i.putExtra(MapsActivity.EXTRA_PARAM_LON, address.get(0).getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                startActivity(i);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

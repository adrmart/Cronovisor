package com.uva.adrmart.cronovisor_v1.activitys;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.domain.MarkerPropio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener, GpsStatus.Listener  {

    private static final String TAG = MapsActivity.class.getName();
    private static final String URL_MARKERS = "http://virtual.lab.inf.uva.es:20202/marker/?format=json";

    private GoogleMap mMap;

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    // Variables de localizacion
    private LocationManager locationManager;
    private Location bestLocation; //Mejor localizacion del dispositivo
    private double lat; //Latitud del usuario
    private double lon; //Longitud del usuario

    private HashMap<LatLng, Integer> hashMarker;
    private List<MarkerPropio> listMarkers;

    private RequestQueue requestQueue;
    private String idioma;
    private MenuItem searchItem;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idioma = Locale.getDefault().getDisplayLanguage();
        Log.d(TAG, idioma);
        //Inicio de la base de datos
        //BDHelper.init(this);


        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(status == ConnectionResult.SUCCESS) {
            //Success! Do what you want
            setContentView(R.layout.activity_maps);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            assert drawer != null;
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            assert navigationView != null;
            navigationView.setNavigationItemSelectedListener(this);

            hashMarker = new HashMap<>();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //Inicio del mapa

        }else{
            GooglePlayServicesUtil.getErrorDialog(status, this, status);
        }

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "startMap");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap = googleMap;

        //Comprobacion de permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (!provider.contains("gps")) {
                AlertNoGps();
            }
            Location location = null;
            //Inicializacion de los providers
            for (String s : locationManager.getAllProviders()) {
                int minDistance = 2;
                int checkInterval = 2;
                locationManager.requestLocationUpdates(s, checkInterval,
                        minDistance, this);
                location = locationManager.getLastKnownLocation(s);
            }
            mMap.setMyLocationEnabled(true);
            if (location!=null){
                Log.d(TAG, "Posicion actual -> LAT: "+ location.getLatitude() + " LON: " + location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
            } else{
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.651981, -4.728561), 16));
            }

        } else {
            // Show rationale and request permission.
        }
        requestQueue= Volley.newRequestQueue(this);
        listMarkers = new ArrayList<>();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(URL_MARKERS, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, String.valueOf(response));
                Log.d("RESQUEST", String.valueOf(Thread.currentThread()));
                if (idioma.equals("español")){
                    for(int i=0; i<response.length(); i++){
                        try {
                            JSONObject objeto= response.getJSONObject(i);
                            MarkerPropio marker = new MarkerPropio(objeto.getString("description_es"),
                                    objeto.getInt("id"),
                                    objeto.getDouble("latitud"),
                                    objeto.getDouble("longitud"),
                                    objeto.getString("title_es"),
                                    objeto.getInt("num_images"));
                            listMarkers.add(marker);
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
                            MarkerPropio marker = new MarkerPropio(objeto.getString("description_en"),
                                    objeto.getInt("id"),
                                    objeto.getDouble("latitud"),
                                    objeto.getDouble("longitud"),
                                    objeto.getString("title_en"),
                                    objeto.getInt("num_images"));
                            listMarkers.add(marker);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error de parsing: "+ e.getMessage());
                        }
                        if (i==response.length()-1){
                            Log.d(TAG,"Ultimo elemento de la respuesta: " + i);
                        }
                    }
                }
                notifyMarkerEnded();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
                Toast.makeText(getBaseContext(), getText(R.string.server_fail), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsArrayRequest);

        Log.d(TAG, "Map listo");

    }

    private void notifyMarkerEnded() {
        addMarkers();
    }

    @Override
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
        Log.d(TAG, "Cambia estado: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Se habilita: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "Se deshabilita: " + provider);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Nueva localización: " + location);
        lat = location.getLatitude();
        lon = location.getLongitude();
        actualizaMejorLocaliz(location);
    }

    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz != null && (bestLocation == null
                || localiz.getAccuracy() < 2 * bestLocation.getAccuracy()
                || localiz.getTime() - bestLocation.getTime() > TWO_MINUTES)) {
            Log.d(TAG, "Nueva mejor localización");
            bestLocation = localiz;
            lat = localiz.getLatitude();
            lon = localiz.getLongitude();
        }
    }

    /**
     * Método que solicita al usuario que active la localizacion GPS
     */
    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Método que agrega los marcadores de la base de datos al mapa
     */

    private void addMarkers() {

        for (MarkerPropio m :  listMarkers) {
            LatLng posicion = new LatLng(m.getLatitud(), m.getLogitud());
            final MarkerOptions marker = new MarkerOptions()
                    .position(posicion)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1))
                    .title(m.getTitulo())
                    .snippet(m.getDescripcion());
            switch (m.getNumImages()){
                case 1:
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1));
                    break;
                case 2:
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2));
                    break;
                case 3:
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker3));
                    break;
                case 4:
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker4));
                    break;
                case 5:
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker5));
                    break;
                case 6:
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker6));
                    break;

            }
            mMap.addMarker(marker);
            Log.d(TAG, posicion +" + " +m.getId());
            hashMarker.put(posicion, m.getId());
            mMap.setOnInfoWindowClickListener(this);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater infrater = getMenuInflater();
        infrater.inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("onQueryTextCahnge", newText);
                return true;
            }
        });
        /*searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        Log.d(TAG, "SearchItem: " + searchItem.toString());
        searchView = (SearchView) searchItem.getActionView();
        Log.d(TAG, "SearchView: " + searchView.toString());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("onQueryTextChange", newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);
                return true;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            onMapReady(mMap);
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(getApplicationContext(), StreetGalleryActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_route) {

        } else if (id == R.id.nav_download) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int id = hashMarker.get(marker.getPosition());
        Log.d(TAG, "Posicion" + marker.getPosition() + " - " + id);
        Iterator i = listMarkers.iterator();
        int numImages = 1;

        while(i.hasNext()){
            MarkerPropio m = (MarkerPropio) i.next();
            Log.d(TAG, m.getId() + " - " + id);
            if (m.getId()==id){
                numImages=m.getNumImages();
                Log.d(TAG, "num " + numImages);
            }
        }

        if (numImages==1){
            Log.d(TAG, "URL: "+ ImageGalleryActivity.URL_FROM_MARKER + id +
                    ImageGalleryActivity.URL_FORMAT);
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(ImageGalleryActivity.URL_FROM_MARKER + id +
                    ImageGalleryActivity.URL_FORMAT, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {

                    // Obtener el marker del objeto
                    for(int i=0; i<response.length(); i++){
                        try {
                            JSONObject objeto= response.getJSONObject(i);
                            int idImagen = objeto.getInt("id");
                            Log.e(TAG, "START INTENT - " + idImagen);
                            Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
                            intent.putExtra(DetalleActivity.EXTRA_PARAM_ID, idImagen);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error de parsing: "+ e.getMessage() + "/// "+ e.getCause());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
                }
            });
            requestQueue.add(jsArrayRequest);

        } else{
            Intent intent = new Intent(this, ImageGalleryActivity.class);
            intent.putExtra(ImageGalleryActivity.EXTRA_PARAM_ID, id);
            intent.putExtra(ImageGalleryActivity.EXTRA_PARAM, 1);

            startActivity(intent);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager.getAllProviders()!=null){
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

}

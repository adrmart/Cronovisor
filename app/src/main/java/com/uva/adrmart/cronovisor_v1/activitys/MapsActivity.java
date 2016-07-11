package com.uva.adrmart.cronovisor_v1.activitys;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

/**
 * Class that models maps and markers
 */

public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener {

    private static final String TAG = MapsActivity.class.getName();
    private static final String URL_MARKERS = "http://virtual.lab.inf.uva.es:20202/marker/?format=json";
    public static final String EXTRA_PARAM_LAT = "com.uva.adrmart.tfg.lat";
    public static final String EXTRA_PARAM_LON = "com.uva.adrmart.tfg.lon";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private GoogleMap mMap;

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private LocationManager locationManager;
    private Location bestLocation; //Best location

    private HashMap<LatLng, Integer> hashMarker;
    private List<MarkerPropio> listMarkers;

    private RequestQueue requestQueue;
    private String idioma;

/*
    private MarkerDao markerDao;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idioma = Locale.getDefault().getDisplayLanguage();

/*
        markerDao = new MarkerDaoImpl();
*/
        Log.d(TAG, idioma);

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
        requestLocationPermision();
        requestMarkers();
        Log.d(TAG, "Map listo");
    }

    /**
     * Check if we have permision to access location
     */
    public void requestLocationPermision(){

        // Forma de actuar diferente si la versión del dispositivo en Marshmallow
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Log.d(TAG, "Versión 23");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Si no tenemos permiso, lo pedimos

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Log.d(TAG, "Show an expanation");
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.show_explanation))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            })
                            .create()
                            .show();

                } else{
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                }
            } else{
                Log.d(TAG, "Tenemos permiso");

                String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (!provider.contains("gps")) {
                    //Si el GPS no esta activo, pedimos su iniciación
                    AlertNoGps();
                }

                //Inicializacion de los providers
                for (String s : locationManager.getAllProviders()) {
                    int minDistance = 2;
                    int checkInterval = 2;
                    locationManager.requestLocationUpdates(s, checkInterval,
                            minDistance, this);
                    Location actualLocation = locationManager.getLastKnownLocation(s);
                    //Comprobamos si es una mejor localización
                    if (actualLocation!=null){
                        if (isBetterLocation(actualLocation)){
                            Log.d(TAG, "Mejor localización -> " + s + " - "+ actualLocation);
                            bestLocation = actualLocation;
                        }
                    }
                }

                mMap.setMyLocationEnabled(true);
                setLocation();
            }
        } else{
            //Comprobacion de permisos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Tenemos permiso");

                String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

                if (!provider.contains("gps")) {
                    //Si el GPS no esta activo, pedimos su iniciación
                    AlertNoGps();
                }

                //Inicializacion de los providers
                for (String s : locationManager.getAllProviders()) {
                    int minDistance = 2;
                    int checkInterval = 2;
                    locationManager.requestLocationUpdates(s, checkInterval,
                            minDistance, this);
                    Location actualLocation = locationManager.getLastKnownLocation(s);
                    if (actualLocation!=null){
                        if (isBetterLocation(actualLocation)){
                            Log.d(TAG, "Mejor localización -> " + s + " - "+ actualLocation);
                            bestLocation = actualLocation;
                        }
                    }
                }

                mMap.setMyLocationEnabled(true);
                setLocation();
            }
        }

    }

    /**
     * Set the camera center
     */
    public void setLocation(){
        //Check if user come from notification
        if (getIntent().hasExtra(EXTRA_PARAM_LAT) && getIntent().hasExtra(EXTRA_PARAM_LON)){
            Log.d(TAG, "Proviene del servicio, lat: " + getIntent().getExtras().getDouble(EXTRA_PARAM_LAT) + " - lon: " + getIntent().getExtras().getDouble(EXTRA_PARAM_LON));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getIntent().getExtras().getDouble(EXTRA_PARAM_LAT), getIntent().getExtras().getDouble(EXTRA_PARAM_LON)), 18));
            NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNM.cancel(1);
        } else{
            if (bestLocation!=null){
                Log.d(TAG, "Posicion actual -> LAT: "+ bestLocation.getLatitude() + " LON: " + bestLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude()), 16));
            } else{
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.651981, -4.728561), 16));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.651981, -4.728561), 16));
                }
            }
        }
    }

    /**
     * Request markers to the server
     */
    private void requestMarkers(){
        requestQueue= Volley.newRequestQueue(this);
        listMarkers = new ArrayList<>();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(URL_MARKERS, new Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
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
                            Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();

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
                            Toast.makeText(getBaseContext(), getText(R.string.internal_fail), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                addMarkers();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
                /*if (markerDao.findMarkers()!=null){
                    listMarkers = markerDao.findMarkers();
                    addMarkers();
                } else {
                    Toast.makeText(getBaseContext(), getText(R.string.server_fail), Toast.LENGTH_LONG).show();
                }*/
                Toast.makeText(getBaseContext(), getText(R.string.server_fail), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsArrayRequest);
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
        if (isBetterLocation(location)){
            Log.d(TAG, "Si es mejor localizacion");
            bestLocation=location;
        } else{
            Log.d(TAG, "No es mejor localizacion");
        }
    }

    protected boolean isBetterLocation(Location location) {
        if (bestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - bestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - bestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                bestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Suggest user to enable GPS
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
     * Add markers to map
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
            hashMarker.put(posicion, m.getId());
            mMap.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_gallery; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        /*MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        Log.d(TAG, "SearchItem: " + searchItem.toString());
        searchView = (SearchView) searchItem.getActionView();
        Geocoder g = new Geocoder(this, Locale.getDefault());
        LatLng lt = new LatLng(41.651841, -4.728340);
        final HashMap<String, LatLng> addressNames = new HashMap<>();
        try {
            List<Address> listAddress = g.getFromLocation(lt.latitude, lt.longitude, 1000);
            for (int i=0;i<listAddress.size();i++){
                Log.d(TAG, "list size: " + listAddress.size());
                String address = listAddress.get(i).getAddressLine(0);
                Log.d(TAG, address);
                LatLng location = new LatLng(listAddress.get(i).getLatitude(),listAddress.get(i).getLongitude());
                int position = address.indexOf(",");
                if (position!=-1){
                    addressNames.put(address.substring(0,position).toUpperCase(), location);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    if (addressNames.containsKey(query.toUpperCase())){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressNames.get(query.toUpperCase()), 16));
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextCahnge", newText);
                    return true;
                }
            });
        }*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

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
            Intent i = new Intent(getApplicationContext(), GalleryStreetActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Start new activity when marker clicked
     * @param marker marker clicked
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        int id = hashMarker.get(marker.getPosition());
        Log.d(TAG, "Posicion" + marker.getPosition() + " - " + id);
        Iterator i = listMarkers.iterator();
        int numImages = 1;

        while(i.hasNext()){
            MarkerPropio m = (MarkerPropio) i.next();
            if (m.getId()==id){
                numImages=m.getNumImages();
            }
        }

        if (numImages==1){

            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(GalleryImageActivity.URL_FROM_MARKER + id +
                    GalleryImageActivity.URL_FORMAT, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {

                    // Obtener el marker del objeto
                    for(int i=0; i<response.length(); i++){
                        try {
                            JSONObject objeto= response.getJSONObject(i);
                            int idImagen = objeto.getInt("id");
                            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                            intent.putExtra(DetailActivity.EXTRA_PARAM_ID, idImagen);
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
            Intent intent = new Intent(this, GalleryImageActivity.class);
            intent.putExtra(GalleryImageActivity.EXTRA_PARAM_ID, id);
            intent.putExtra(GalleryImageActivity.EXTRA_PARAM, 1);
            intent.putExtra(GalleryImageActivity.EXTRA_PARAM_NAME_MARKER, marker.getTitle());
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(locationManager!=null){
                if (locationManager.getAllProviders()!=null){
                    locationManager.removeUpdates(this);
                }
            }
        }
    }

}

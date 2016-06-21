package com.uva.adrmart.cronovisor_v1.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.activitys.MapsActivity;
import com.uva.adrmart.cronovisor_v1.domain.Street;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getName();
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;
    private Handler mHandler;

    private static final String URL_STREETS= "http://virtual.lab.inf.uva.es:20202/street/?format=json";

    private LocationManager locationManager;
    private ArrayList<Street> listStreet;
    private RequestQueue requestQueue;
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        requestQueue= Volley.newRequestQueue(this);
        listStreet = new ArrayList<>();
        // Display a notification about us starting.  We put an icon in the status bar.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        mHandler = new android.os.Handler();
        requestStreets();
        return START_STICKY;
    }
    private void requestStreets(){

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(URL_STREETS, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // Obtener el marker del objeto
                Log.d(TAG, "onResponse: "+ Thread.currentThread());
                if (Locale.getDefault().getDisplayLanguage().equals("español")){
                    for(int i=0; i<response.length(); i++){
                        try {
                            JSONObject objeto= response.getJSONObject(i);
                            Street street = new Street(objeto.getString("description_es"),
                                    objeto.getInt("id"),
                                    objeto.getDouble("latitud"),
                                    objeto.getDouble("longitud"),
                                    objeto.getString("name_es"),
                                    objeto.getString("represent"));
                            listStreet.add(street);
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
                            Street street = new Street(objeto.getString("description_en"),
                                    objeto.getInt("id"),
                                    objeto.getDouble("latitud"),
                                    objeto.getDouble("longitud"),
                                    objeto.getString("name_en"),
                                    objeto.getString("represent"));
                            listStreet.add(street);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error de parsing: "+ e.getMessage());
                        }
                        if (i==response.length()-1){
                            Log.d(TAG,"Ultimo elemento de la respuesta: " + i);
                        }
                    }
                }
                ping();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
            }
        });
        requestQueue.add(jsArrayRequest);
    }
    private void ping() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permiso");
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            Location selfLocation = null;
            if (provider.contains("gps")) {
                Log.d(TAG, "contains gps");
                //Inicializacion de los providers
                for (String s : locationManager.getAllProviders()) {
                    if(locationManager.getLastKnownLocation(s)!=null){
                        selfLocation = locationManager.getLastKnownLocation(s);
                    }
                    if (selfLocation!=null){
                        Log.d(TAG, s + " - " + selfLocation.toString());
                    } else {
                        Log.d(TAG, s + " - es null");
                    }

                }
                Log.d(TAG, "Localizacion: " + selfLocation);
                for (int i=0;i<listStreet.size();i++){
                    Location streetLocation = new Location("A");
                    streetLocation.setLatitude( listStreet.get(i).getLat());
                    streetLocation.setLongitude( listStreet.get(i).getLon());
                    //Log.d(TAG, listStreet.get(i).getNombre() + " distancia: " +streetLocation.distanceTo(selfLocation) );
                    if (streetLocation.distanceTo(selfLocation)<10){
                        showNotification(listStreet.get(i));
                    }

                }
            }

        }
        scheduleNext();
    }

    private void scheduleNext() {

        mHandler.postDelayed(new Runnable() {
            public void run() { ping(); }
        }, 300000);
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(Street street) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapsActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.marker1)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.app_name))  // the label of the entry
                .setContentText("Estas en la calle: "+ street.getNombre() + " en coordenadas" +  street.getLat()+ " - " + street.getLat())  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}

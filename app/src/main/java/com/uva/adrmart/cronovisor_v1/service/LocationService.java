package com.uva.adrmart.cronovisor_v1.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.activitys.GalleryImageActivity;
import com.uva.adrmart.cronovisor_v1.activitys.MapsActivity;
import com.uva.adrmart.cronovisor_v1.database.DBHelper;
import com.uva.adrmart.cronovisor_v1.domain.Street;
import com.uva.adrmart.cronovisor_v1.persistence.NotificationDao;
import com.uva.adrmart.cronovisor_v1.persistence.NotificationDaoImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Service android to track location of user and show notifications when
 * near a street that is in database
 */
public class LocationService extends Service {
    private static final String TAG = LocationService.class.getName();
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private final int NOTIFICATION = 1;
    private Handler mHandler;

    private static final String URL_STREETS= "http://virtual.lab.inf.uva.es:20202/street/?format=json";

    private ArrayList<Street> listStreet;
    private RequestQueue requestQueue;
    private HashMap<Integer, Integer> listaNotificaciones;
    private NotificationDao notificationDao;

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        requestQueue= Volley.newRequestQueue(this);
        listStreet = new ArrayList<>();
        DBHelper.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        mHandler = new android.os.Handler();
        notificationDao = new NotificationDaoImpl();
        listaNotificaciones = notificationDao.getNotifications();
        requestStreets();
        return START_STICKY;
    }

    /**
     * Request streets to the server
     */
    private void requestStreets(){

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(URL_STREETS, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // Obtener el marker del objeto
                if (Locale.getDefault().getDisplayLanguage().equals("español")){
                    for(int i=0; i<response.length(); i++){
                        try {
                            JSONObject objeto= response.getJSONObject(i);
                            Street street = new Street(objeto.getString("description_es"),
                                    objeto.getInt("id"),
                                    objeto.getString("name_es"),
                                    objeto.getString("represent"));
                            if (!listaNotificaciones.containsKey(street.getId())){
                                listStreet.add(street);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error de parsing: "+ e.getMessage());
                        }
                    }
                } else {
                    for(int i=0; i<response.length(); i++){
                        try {
                            JSONObject objeto= response.getJSONObject(i);
                            Street street = new Street(objeto.getString("description_en"),
                                    objeto.getInt("id"),
                                    objeto.getString("name_es"),
                                    objeto.getString("represent"));
                            if (!listaNotificaciones.containsKey(street.getId())){
                                listStreet.add(street);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error de parsing: "+ e.getMessage());
                        }
                    }
                }
                try {
                    ping();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
                //if error, wait and try again
                mHandler.postDelayed(new Runnable() {
                    public void run() { requestStreets();
                    }
                }, 100000);

            }
        });
        requestQueue.add(jsArrayRequest);
    }

    /**
     * Check location of user
     * @throws IOException
     */
    private void ping() throws IOException {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            Location selfLocation = null;
            if (provider.contains("gps")) {
                //Inicializacion de los providers
                for (String s : locationManager.getAllProviders()) {
                    Location aux = null;
                    if(locationManager.getLastKnownLocation(s)!=null){
                        aux = locationManager.getLastKnownLocation(s);
                    }
                    if (selfLocation==null && aux!=null){
                        selfLocation = aux;
                    } else if (aux != null && selfLocation.getAccuracy() > aux.getAccuracy()) {
                        selfLocation = aux;
                    }
                }
                if (selfLocation!=null){
                    Geocoder g = new Geocoder(this, Locale.getDefault());
                    List <Address> addressList = g.getFromLocation(selfLocation.getLatitude(),
                            selfLocation.getLongitude(), 1);
                    String address = addressList.get(0).getAddressLine(0);
                    int position = address.indexOf(",");
                    String addressName;
                    if (position>0){
                        addressName = address.substring(0, position);
                    } else{
                        addressName = address;
                    }
                    //Toast.makeText(getBaseContext(),"Estas en la calle: " + addressName, Toast.LENGTH_LONG).show();
                    for (int i=0;i<listStreet.size();i++){
                        Street street = listStreet.get(i);
                        if (addressName.toUpperCase().contains(street.getNombre().toUpperCase())){
                            listStreet.remove(i);
                            notificationDao.addNotification(street.getId());
                            showNotification(street, addressList.get(0));
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Fin de comprobacion");
        scheduleNext();
    }

    private void scheduleNext() {

        mHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    ping();
                } catch (IOException e) {
                    e.printStackTrace();
                    scheduleNext();
                }
            }
        }, 300000);
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        Log.d(TAG, "Se ha detenido el servicio");
        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Show a notification when the user is near a street.
     */
    private void showNotification(Street street, Address address) {
        Intent intentMap = new Intent (this, MapsActivity.class);

        intentMap.putExtra(MapsActivity.EXTRA_PARAM_LAT, address.getLatitude());
        intentMap.putExtra(MapsActivity.EXTRA_PARAM_LON, address.getLongitude());

        Intent intentStreet = new Intent (this, GalleryImageActivity.class);
        intentStreet.putExtra(GalleryImageActivity.EXTRA_PARAM_ID, street.getId());
        intentStreet.putExtra(GalleryImageActivity.EXTRA_PARAM, 3);
        intentStreet.putExtra(GalleryImageActivity.EXTRA_PARAM_NAME_STREET, street.getNombre());

        PendingIntent prevPendingIntentMap = PendingIntent.getActivity(this, 1, intentMap, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent prevPendingIntentImage = PendingIntent.getActivity(this, 1, intentStreet, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the info for the views that show in the notification panel.
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_map_black_18dp)  // the status icon
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(street.getNombre())  // the label of the entry
                    .setContentText("Existen imagenes cerca de su localización")  // the contents of the entry
                    .setAutoCancel(true)
                    .addAction(new Notification.Action.Builder(R.drawable.ic_place_black_18dp, getString(R.string.notification_button_map), prevPendingIntentMap).build())
                    .addAction(new Notification.Action.Builder(R.drawable.ic_collections_black_18dp, getString(R.string.notification_button_images), prevPendingIntentImage).build())
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

        } else{
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_map_black_18dp)  // the status icon
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(street.getNombre())  // the label of the entry
                    .setContentText("Existen imagenes cerca de su localización")  // the contents of the entry // The intent to send when the entry is clicked
                    .addAction(R.drawable.ic_place_black_18dp, getString(R.string.notification_button_map), prevPendingIntentMap)
                    .addAction(R.drawable.ic_collections_black_18dp, getString(R.string.notification_button_images), prevPendingIntentImage)
                    .setAutoCancel(true)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }
        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}

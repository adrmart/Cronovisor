package com.uva.adrmart.cronovisor_v1.activitys;

/** Splash screen
 * Starts LocationService
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.service.LocationService;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends Activity {

    private static final String TAG = SplashScreenActivity.class.getName();

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (!isMyServiceRunning(LocationService.class)){
            Log.d(TAG, "Servicio no activo");
            startService(new Intent(this, LocationService.class));
        } else{
            Log.d(TAG, "Servicio ya activo");
        }

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash_screen);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // Start the next activity
                Intent mainIntent = new Intent().setClass(
                        SplashScreenActivity.this, MapsActivity.class);
                startActivity(mainIntent);

                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

    /**
     * Check if location service is running
     * @param serviceClass class to check if is running
     * @return true is running, false isn´t running
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
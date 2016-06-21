package com.uva.adrmart.cronovisor_v1.activitys;

/**
 * Created by Adrian on 07/06/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
        //startService(new Intent(Intent.ACTION_SYNC, null,SplashScreenActivity.this, LocationService.class));
       startService(new Intent(this, LocationService.class));
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


}
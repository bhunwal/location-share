package com.locationupdates;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationResult;
import com.locationupdates.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by HP-HP on 26-11-2015.
 */
public class LocationUpdatesFromGPS extends IntentService {

    private String TAG = this.getClass().getSimpleName();

    static final public String LOCATION_RESULT_FROM_GPS = "LocationUpdatesFromGPS.request";

    static final public String LOATION_MESSAGE_GPS = "com.locationupdates.LocationUpdatesFromGPS.result";

    LocalBroadcastManager broadcaster;

    public LocationUpdatesFromGPS() {
        super("Fused Location");
    }

    public LocationUpdatesFromGPS(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "got new location");
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.i(TAG, "got new location" + location);
                BroadcastLocation(location);
            } else {
                Log.e(TAG, "got empty location");
            }
        }
    }

    private void BroadcastLocation(Location location) {
        Intent intent = new Intent(LOCATION_RESULT_FROM_GPS);
        intent.putExtra(LOATION_MESSAGE_GPS, location);
        broadcaster.sendBroadcast(intent);
    }


}

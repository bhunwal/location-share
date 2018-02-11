package com.locationupdates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.locationupdates.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.locationupdates.utils.CheckPermissionAndNetworkState.alertUser;
import static com.locationupdates.utils.CheckPermissionAndNetworkState.isLocationEnabled;
import static com.locationupdates.utils.CheckPermissionAndNetworkState.isNetworkConnectionAvailable;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TimeZone timeZone = TimeZone.getDefault();

    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 500;

    private String TAG = this.getClass().getSimpleName();

    private boolean startAsSharingDevice = true;
    private String CAB_ID;

    private int SECONDS = 1000;
    BroadcastReceiver receiver;

    private Context context;

    private TextView textView;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        textView = (TextView) findViewById(R.id.showStatus);

        checkPermissions();
        if (!isNetworkConnectionAvailable(this)) {
            alertUser(this, "No internet Connection", "Please turn on internet connection to continue");
        }





        this.context = this;
        Intent intent = getIntent();
        CAB_ID = intent.getStringExtra(StartScreenActivity.CAB_ID);
        String message = intent.getStringExtra(StartScreenActivity.EXTRA_MESSAGE);
        if (message.equals(StartScreenActivity.SHARE_LOCATION)) {

            startAsSharingDevice = true;

        } else {
            startAsSharingDevice = false;

        }

        if (startAsSharingDevice) {
            if (!isLocationEnabled(this)) {
                alertUser(this, "Location sharing not enaled", "Please turn on location sharing to continue");
            }
            startbroadcastReciever(); //start listening to location
            startSharingLocationGPS();//start getting location from GPS
        } else {
            textView.setVisibility(View.VISIBLE);
            handler.post(runnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startbroadcastReciever() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "received location form GPS/SERVER");
                String action = intent.getAction();
                if (action != null) {
                    if (action.equals(LocationUpdatesFromGPS.LOCATION_RESULT_FROM_GPS)) {
                        Location location = intent.getParcelableExtra(LocationUpdatesFromGPS.LOATION_MESSAGE_GPS);
                        Log.i(TAG, "got new location from GPS " + location);
                        handleNewLocation(location);
                        sendLocationToServer(location);
                    }
//                    else if (action.equals(GetLocationFromServer.LOCATION_RESULT_FROM_SERVER)) {
//                        Location location = intent.getParcelableExtra(GetLocationFromServer.LOCATION_MESSAGE_FROM_SERVER);
//                        handleNewLocation(location);
//                        Log.i(TAG, "got location from server " + location);
//                    }
                }
            }
        };
    }

    void startSharingLocationGPS() {
        Log.i(TAG, "starting gps location share");
        Intent intent = new Intent(Map.this, BackgroundLocationService.class);
        intent.putExtra(StartScreenActivity.CAB_ID, CAB_ID);
        startService(intent);
    }

    void stopSharingLocation() {
        Log.i(TAG, "stopping gps location share");
        Intent intent = new Intent(Map.this, BackgroundLocationService.class);
        stopService(intent);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.clear();
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.animateCamera(zoom);

        CircleOptions co = new CircleOptions();
        co.center(latLng);
        co.radius(location.getAccuracy());
//        co.fillColor(ResourcesCompat.getColor(getResources(), R.color.lightBlue, null));
        co.fillColor(0x5587CEFA);
        co.strokeColor(ResourcesCompat.getColor(getResources(), R.color.darkBlue, null));
        co.strokeWidth(2);
        mMap.addCircle(co);
    }

    @Override
    protected void onStart() {

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(LocationUpdatesFromGPS.LOCATION_RESULT_FROM_GPS)
        );
//        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
//                new IntentFilter(GetLocationFromServer.LOCATION_RESULT_FROM_SERVER)
//        );
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        stopSharingLocation();
        super.onDestroy();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            VolleySingleton.getInstance(context).getRequestQueue().add(getLocationRequest());
            handler.postDelayed(runnable, 10 * SECONDS);
        }
    };

    private JsonObjectRequest getLocationRequest() {
        String url = getString(R.string.getUpdateUrl) + "?name=" + CAB_ID;
        Log.i(TAG, "sending get request");
        JsonObjectRequest stringRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "got response from server " + response.toString());
                        if(ResponseParser.isSuccessful(getApplicationContext(), response)) {
                            Location location = new LocationObject(response).getLocation();
                            if (location != null) {
                                handleNewLocation(location);
                                textView.setText(getTimeStringFromResponse(response));
                            }
                        }
                        else {
                            textView.setText("having problem in access current location");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "error occured " + error.toString());//TODO do something?
                    }
                });
        return stringRequest;
    }

//    private String getTimeStringFromResponse(JSONObject response) {
//        String formattedDate = null;
//        try {
//
//            if (responseTime != null) {
//                Log.i(TAG, "time: "+responseTime);
//                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
//                format.setTimeZone(timeZone);
//                formattedDate = format.format(responseTime);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return formattedDate;
//    }

    private String getTimeStringFromResponse(JSONObject response) {
        String formattedDate = null;
        try {
            String dateString = response.getString("time");
            if (dateString != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Date date = formatter.parse(dateString.replaceAll("Z$", "+0000"));
                formatter.setTimeZone(timeZone);
                formattedDate = "updated at "+ formatter.format(date);
                System.out.println("" + formattedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    private void sendLocationToServer(Location location) {
        JsonObjectRequest jsonObjectRequest = PostLocationReqest(location);
        if (jsonObjectRequest != null) {
            VolleySingleton.getInstance(this).getRequestQueue().add(jsonObjectRequest);
            //TODO show that you are sharing your location
        } else {
            //TODO show and throw error
        }
    }

    private JsonObjectRequest PostLocationReqest(final Location location) {
        String url = getString(R.string.sendUpdateUrl);
        final JSONObject jsonBody;
        try {
            jsonBody = new JSONObject(new LocationObject(location, CAB_ID).toJson());
            Log.i(TAG, "json body = " + jsonBody);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "got response from server: " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "got error response from server: " + error.getMessage());
                }
            }) {
                @Override
                protected java.util.Map<String, String> getParams() throws AuthFailureError {
                    return super.getParams();
                }

                @Override
                public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                    return super.getHeaders();
                }
            };
            return request;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void StartGettingLocationFromServer() {
//        Log.i(TAG, "scheduling alarm");
//        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1 * SECONDS, pIntent);
//    }
//
//    public void StopGettingLocationFromServer() {
//        Log.i(TAG, "cancelling alarm");
//        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pIntent);
//    }


    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission not there");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "permission granted");
                // permission was granted, yay! Do the task you need to do.

            } else {
                Log.i(TAG, "permission not granted");
                checkPermissions();
            }
            return;
        }
    }
}

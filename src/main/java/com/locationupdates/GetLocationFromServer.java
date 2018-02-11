//package com.locationupdates;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.location.Location;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.locationupdates.utils.VolleySingleton;
//
//import org.json.JSONObject;
//
//public class GetLocationFromServer extends IntentService {
//
//    static final public String LOCATION_RESULT_FROM_SERVER = "got.location.from.";
//    static final public String LOCATION_MESSAGE_FROM_SERVER = "got.location.server.message";
//    LocalBroadcastManager broadcaster;
//
//    String TAG = this.getClass().getSimpleName();
//
//    public GetLocationFromServer() {
//        super("GetLocationFromServer");
//    }
//
//
//    @Override
//    public void onCreate() {
//        Log.i(TAG, "oncreate called");
//        broadcaster = LocalBroadcastManager.getInstance(this);
//        super.onCreate();
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.i(TAG, "gettting location from server");
//        getLocation();
//    }
//
//    private void getLocation() {
//        VolleySingleton.getInstance(this).getRequestQueue().add(getLocationRequest());
//    }
//
//    private void BroadCastLocation(Location location) {
//        if (location != null) {
//            Intent intent = new Intent(LOCATION_RESULT_FROM_SERVER);
//            intent.putExtra(LOCATION_MESSAGE_FROM_SERVER, location);
//            broadcaster.sendBroadcast(intent);
//        } else {
//            //TODO show some error is server is not responding correctly
//        }
//    }
//
//    private JsonObjectRequest getLocationRequest() {
//        String url = getString(R.string.getUpdateUrl) + "?name=cab1";
//        Log.i(TAG, "sending get request");
//        JsonObjectRequest stringRequest = new JsonObjectRequest(url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(TAG, "gor response from server " + response.toString());
//
//                        BroadCastLocation(new LocationObject(response).getLocation());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.i(TAG, "error occured " + error.toString());
//                    }
//                });
//        return stringRequest;
//    }
//
//}

//package com.locationupdates;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//public class AlarmReceiver extends BroadcastReceiver {
//
//    public static final int REQUEST_CODE = 12345;
//
//    public AlarmReceiver() {
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Log.i("AlarmReceiver", "onReceived alarm");
//        Intent i = new Intent(context, GetLocationFromServer.class);
//        i.putExtra("foo", "bar");
//        context.startService(i);
//    }
//}

package com.locationupdates;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mbhunwal on 3/26/17.
 */
public class LocationObject {

    Location location;
    String info;

    public LocationObject(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            this.location = new Location(jsonObject.getString("location"));
            this.info = jsonObject.getString("info");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public LocationObject(JSONObject jsonObject) {
        Location location = new Location("service Provider");
        try {
            JSONObject locationObject = jsonObject.getJSONObject("location");
            if(locationObject != null) {
                location.setLatitude(locationObject.getDouble("latitude"));
                location.setLongitude(locationObject.getDouble("longitude"));
                location.setAccuracy(locationObject.getLong("accuracy"));
                location.setTime(locationObject.getLong("time"));
                this.location = location;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LocationObject(Location location, String info) {
        this.location = location;
        this.info = info;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        JSONObject location = new JSONObject();
        try {
            location.put("longitude", getLocation().getLongitude());
            location.put("latitude", getLocation().getLatitude());
            location.put("time", getLocation().getTime());
            location.put("accuracy", getLocation().getAccuracy());
            json.put("name", getInfo());
            json.put("info", getInfo());
            json.put("location", location);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}

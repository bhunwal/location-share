package com.locationupdates;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mbhunwal on 4/24/17.
 */
public class ResponseParser {

    public static boolean isSuccessful(Context context, JSONObject jsonObject) {

        try {
            String message = jsonObject.getString("message");
            if(message.equals(context.getString(R.string.SuccessMessage)))
            {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static Location getLocationFromResponse(JSONObject jsonObject) {
        return null;
    }

}

package com.example.comfortzone.initial.data;

import static com.example.comfortzone.initial.ui.LoginActivity.KEY_EMAIL;
import static com.example.comfortzone.initial.ui.LoginActivity.KEY_FIELDS;
import static com.example.comfortzone.initial.ui.LoginActivity.KEY_NAME;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

public class FacebookLoginAPI {

    public static void getUserDetailFromFB(SaveCallback callback) {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
            ParseUser user = ParseUser.getCurrentUser();
            try {
                if (object.has(KEY_NAME))
                    user.setUsername(object.getString(KEY_NAME));
                if (object.has(KEY_EMAIL))
                    user.setEmail(object.getString(KEY_EMAIL));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            user.saveInBackground(callback);
        });

        Bundle parameters = new Bundle();
        parameters.putString(KEY_FIELDS, String.format("%s,%s", KEY_NAME, KEY_EMAIL));
        request.setParameters(parameters);
        request.executeAsync();
    }
}

package com.example.comfortzone.utils;

import com.parse.ParseUser;

public class UserPreferenceUtil {

    public static final String TAG = "UserPreferenceUtil";
    public static final String KEY_SAVED_CITIES = "savedCities";

    public static void saveCity(ParseUser currentUser, int cityId) {
        currentUser.addUnique(KEY_SAVED_CITIES, cityId);
        currentUser.saveInBackground();
    }

}

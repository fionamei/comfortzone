package com.example.comfortzone.utils;

import android.app.Activity;
import android.util.Log;

import com.example.comfortzone.ui.HostActivity;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserPreferenceUtil {

    public static final String TAG = "UserPreferenceUtil";
    public static final String KEY_SAVED_CITIES = "savedCities";

    public static void saveCity(ParseUser currentUser, int cityId, Activity activity) {
        HashSet<Integer> savedCityIds = ((HostActivity) activity).getSavedCities();
        savedCityIds.add(cityId);
        currentUser.addUnique(KEY_SAVED_CITIES, cityId);
        currentUser.saveInBackground();
    }

    public static void deleteSavedCity(ParseUser currentUser, int cityId, Activity activity) {
        HashSet<Integer> savedCityIds = ((HostActivity) activity).getSavedCities();
        savedCityIds.remove(cityId);
        List<Integer> toRemove = new ArrayList<>();
        toRemove.add(cityId);
        currentUser.removeAll(KEY_SAVED_CITIES, toRemove);
        currentUser.saveInBackground();
    }

    public static boolean isCityAlreadySaved (int cityId, Activity activity) {
        HashSet<Integer> savedCityIds = ((HostActivity) activity).getSavedCities();
        if (savedCityIds != null && savedCityIds.contains(cityId)) {
            return true;
        }
        return false;
    }

}

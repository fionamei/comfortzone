package com.example.comfortzone.utils;

import android.app.Activity;

import com.example.comfortzone.callback.UserDetailsProvider;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserPreferenceUtil {

    public static final String TAG = "UserPreferenceUtil";
    public static final String KEY_SAVED_CITIES = "savedCities";

    public static void saveCity(ParseUser currentUser, int cityId, Activity activity) {
        Set<Integer> savedCityIds = ((UserDetailsProvider) activity).getSavedCities();
        savedCityIds.add(cityId);
        currentUser.addUnique(KEY_SAVED_CITIES, cityId);
        currentUser.saveInBackground();
    }

    public static void deleteSavedCity(ParseUser currentUser, int cityId, Activity activity) {
        Set<Integer> savedCityIds = ((UserDetailsProvider) activity).getSavedCities();
        savedCityIds.remove(cityId);
        List<Integer> toRemove = new ArrayList<>();
        toRemove.add(cityId);
        currentUser.removeAll(KEY_SAVED_CITIES, toRemove);
        currentUser.saveInBackground();
    }

    public static boolean isCityAlreadySaved (int cityId, Activity activity) {
        Set<Integer> savedCityIds = ((UserDetailsProvider) activity).getSavedCities();
        if (savedCityIds != null && savedCityIds.contains(cityId)) {
            return true;
        }
        return false;
    }

}

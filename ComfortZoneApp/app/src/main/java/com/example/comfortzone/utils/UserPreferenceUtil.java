package com.example.comfortzone.utils;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserPreferenceUtil {

    public static final String TAG = "UserPreferenceUtil";
    public static final String KEY_SAVED_CITIES = "savedCities";

    public static void saveCity(ParseUser currentUser, int cityId) {
        currentUser.addUnique(KEY_SAVED_CITIES, cityId);
        currentUser.saveInBackground();
    }

    public static void deleteSavedCity(ParseUser currentUser, int cityId) {
        List<Integer> cityIdToRemove = new ArrayList<>();
        cityIdToRemove.add(cityId);
        currentUser.removeAll(KEY_SAVED_CITIES, cityIdToRemove);
        currentUser.saveInBackground();
    }

    public static boolean isCityAlreadySaved (ParseUser currentUser, int cityId) {
        List<Integer> savedCityIds = (List<Integer>) currentUser.get(KEY_SAVED_CITIES);
        return savedCityIds.contains(cityId);
    }

}

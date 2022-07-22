package com.example.comfortzone.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

import com.example.comfortzone.callback.UserDetailsProvider;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserPreferenceUtil {

    public static final String TAG = "UserPreferenceUtil";
    public static final String KEY_SAVED_CITIES = "savedCities";
    public static final String KEY_IS_FAHRENHEIT = "isFahrenheit";

    public static void saveCity(ParseUser currentUser, int cityId, Activity activity) {
        Set<Integer> savedCityIds = ((UserDetailsProvider) activity).getSavedCities();
        savedCityIds.add(cityId);
        currentUser.addUnique(KEY_SAVED_CITIES, cityId);
        currentUser.saveInBackground();
    }

    public static void deleteSavedCity(ParseUser currentUser, int cityId, Activity activity) {
        Set<Integer> savedCityIds = ((UserDetailsProvider) activity).getSavedCities();
        savedCityIds.remove(cityId);
        List<Integer> cityIdToRemove = new ArrayList<>();
        cityIdToRemove.add(cityId);
        currentUser.removeAll(KEY_SAVED_CITIES, cityIdToRemove);
        currentUser.saveInBackground();
    }

    public static boolean isCityAlreadySaved (int cityId, Activity activity) {
        Set<Integer> savedCityIds = ((UserDetailsProvider) activity).getSavedCities();
        return savedCityIds != null && savedCityIds.contains(cityId);
    }

    public static int convertFahrenheitToCelsius(double degreesFahrenheit) {
        return (int) (((double) 5/9) * (degreesFahrenheit - 32));
    }

    public static void changeDegrees(Boolean[] isFahrenheit, TextView tvCelsius, TextView tvFahrenheit, TextView viewToChange, int temp) {
        if (isFahrenheit[0]) {
            tvCelsius.setTypeface(Typeface.DEFAULT_BOLD);
            tvFahrenheit.setTypeface(Typeface.DEFAULT);
            int celsius = UserPreferenceUtil.convertFahrenheitToCelsius(temp);
            viewToChange.setText(String.valueOf(celsius));
        } else {
            tvFahrenheit.setTypeface(Typeface.DEFAULT_BOLD);
            tvCelsius.setTypeface(Typeface.DEFAULT);
            viewToChange.setText(String.valueOf(temp));
        }
    }
}

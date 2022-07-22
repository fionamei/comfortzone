package com.example.comfortzone.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.TextView;

import com.example.comfortzone.R;
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

    public static int convertCelsiusToFahrenheit(double degreesCelsius) {
        return (int) ((degreesCelsius * ((double) 9/5)) + 32);
    }

    public static void degreeConversion(Activity activity, TextView viewToChange, int temp) {
        boolean isFahrenheit = ((UserDetailsProvider) activity).getIsFahrenheit();
        if (isFahrenheit) {
            viewToChange.setText(String.valueOf(temp));
        } else {
            int celsius = convertFahrenheitToCelsius(temp);
            viewToChange.setText(String.valueOf(celsius));
        }
    }

    public static void switchBoldedDegree(Activity activity, TextView tvCelsius, TextView tvFahrenheit) {
        boolean isFahrenheit = ((UserDetailsProvider) activity).getIsFahrenheit();
        if (isFahrenheit) {
            tvFahrenheit.setTypeface(Typeface.DEFAULT_BOLD);
            tvCelsius.setTypeface(Typeface.DEFAULT);
        } else {
            tvCelsius.setTypeface(Typeface.DEFAULT_BOLD);
            tvFahrenheit.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void updateIsFahrenheitLocally(Activity activity, boolean bool) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getString(R.string.key_is_fahrenheit), bool);
        editor.apply();
    }

}

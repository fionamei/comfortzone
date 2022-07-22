package com.example.comfortzone.flight.utils;

import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;

import com.example.comfortzone.data.local.AllWeathersDatabase;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.WeatherData;
import com.google.android.material.slider.RangeSlider;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FilteringUtils {


    public static void comfortLevelFilter(RangeSlider rsComfortFilter, List<WeatherData> cityList, AllWeathersDatabase db) {
        List<Float> values = rsComfortFilter.getValues();
        int lowComfort = values.get(0).intValue();
        int highComfort = values.get(1).intValue();
        ParseUser currentUser = ParseUser.getCurrentUser();
        int lowRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS))
                .get(lowComfort).getLowRange();
        int highRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS))
                .get(highComfort).getHighRange();
        cityList.clear();
        cityList.addAll(db.weatherDao().getRange(lowRange, highRange));
    }

    public static void sortAlphabetical(List<WeatherData> cityList) {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getCity().compareTo(o2.getCity());
            }
        });
    }

    public static void sortIncTemp(List<WeatherData> cityList) {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getTempData().getTemp() < o2.getTempData().getTemp() ? -1 : 1;
            }
        });
    }

    public static void sortDecTemp(List<WeatherData> cityList) {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getTempData().getTemp() > o2.getTempData().getTemp() ? -1 : 1;
            }
        });
    }

    public static void sortIncRank(List<WeatherData> cityList) {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getRank() < o2.getRank() ? -1 : 1;
            }
        });
    }

    public static void sortDecRank(List<WeatherData> cityList) {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getRank() > o2.getRank() ? -1 : 1;
            }
        });
    }

    public static void sortIncDist(List<WeatherData> cityList) {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getDistanceBetween() > o2.getDistanceBetween() ? -1 : 1;
            }
        });
    }

    public static void sortDecDist(List<WeatherData> cityList) {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getDistanceBetween() < o2.getDistanceBetween() ? -1 : 1;
            }
        });
    }

    public static List<WeatherData> searchCity(CharSequence name, List<WeatherData> cities) {
        return cities.stream().filter(city -> city.getCity().toLowerCase(Locale.ROOT).contains(name.toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList());
    }

}

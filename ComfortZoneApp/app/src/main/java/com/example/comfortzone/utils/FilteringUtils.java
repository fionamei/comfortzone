package com.example.comfortzone.utils;

import com.example.comfortzone.models.WeatherData;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FilteringUtils {

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

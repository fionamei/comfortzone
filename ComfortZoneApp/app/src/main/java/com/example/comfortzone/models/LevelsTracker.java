package com.example.comfortzone.models;

import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_TEMP_AVERAGE;
import static com.example.comfortzone.utils.ComfortCalcUtil.MAX_TEMP;
import static com.example.comfortzone.utils.ComfortCalcUtil.MIN_TEMP;

import com.example.comfortzone.utils.ComfortLevelUtil;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParseClassName("LevelsTracker")
public class LevelsTracker extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_COUNT = "count";
    public static final String KEY_ENTRIESLIST = "entriesList";
    public static final String TAG = "LevelsTracker";
    public static final String KEY_AVERAGE = "average";
    public static final String KEY_LOW_RANGE = "lowRange";
    public static final String KEY_HIGH_RANGE = "highRange";

    public LevelsTracker() {};

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getLevel() {
        try {
            return fetchIfNeeded().getInt(KEY_LEVEL);
        } catch (ParseException e) {
            e.printStackTrace();
        } return -1;

    }

    public void setLevel(int level) {
        put(KEY_LEVEL, level);
    }

    public int getCount()  {
        try {
            return fetchIfNeeded().getInt(KEY_COUNT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return 0;
    }

    public int getAverage() {
        try {
            return fetchIfNeeded().getInt(KEY_AVERAGE);
        } catch (ParseException e) {
            e.printStackTrace();
        } return MIN_TEMP;
    }

    public int getTempAverage() {
        try {
            return fetchIfNeeded().getInt(KEY_TEMP_AVERAGE);
        } catch (ParseException e) {
            e.printStackTrace();
        } return MIN_TEMP;
    }

    public int getLowRange() {
        try {
            return fetchIfNeeded().getInt(KEY_LOW_RANGE);
        } catch (ParseException e) {
            e.printStackTrace();
        } return MIN_TEMP;
    }

    public void setLowRange(int minTemp) {
        put(KEY_LOW_RANGE, minTemp);
    }

    public void setHighRange(int highTemp) {
        put(KEY_HIGH_RANGE, highTemp);
    }

    public int getHighRange() {
        try {
            return fetchIfNeeded().getInt(KEY_HIGH_RANGE);
        } catch (ParseException e) {
            e.printStackTrace();
        } return MAX_TEMP;
    }

    public void setAverage(int avg) {
        put(KEY_AVERAGE, avg);
    }

    public void increaseCount() {
        put(KEY_COUNT, getCount() + 1);
    }

    public void decreaseCount() {
        put(KEY_COUNT, getCount() - 1);
    }

    public void addEntry(ComfortLevelEntry entry) {
        add(KEY_ENTRIESLIST, entry);
    }

    public void removeEntry(ComfortLevelEntry entry) {
        ArrayList<ComfortLevelEntry> toRemove = new ArrayList<>();
        toRemove.add(entry);
        removeAll(KEY_ENTRIESLIST, toRemove);
        decreaseCount();
        saveInBackground();
    }

    public ArrayList<ComfortLevelEntry> getComfortEntriesList() {
        try {
            return (ArrayList<ComfortLevelEntry>) fetchIfNeeded().get(KEY_ENTRIESLIST);
        } catch (ParseException e) {
            e.printStackTrace();
        } return new ArrayList<>();
    }
}

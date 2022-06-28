package com.example.comfortzone.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ParseClassName("LevelsTracker")
public class LevelsTracker extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_COUNT = "count";
    public static final String KEY_ENTRIESLIST = "entriesList";
    public static final String TAG = "LevelsTracker";

    public LevelsTracker() {};

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getLevel() {
        return getInt(KEY_LEVEL);
    }

    public void setLevel(int level) {
        put(KEY_LEVEL, level);
    }

    public int getCount() {
        return getInt(KEY_COUNT);
    }

    public void increaseCount() {
        put(KEY_COUNT, getCount() + 1);
    }

    public void addEntry(ComfortLevelEntry entry) {
        add(KEY_ENTRIESLIST, entry);
    }

    public void removeEntry(ComfortLevelEntry entry) {
        ArrayList<ComfortLevelEntry> toRemove = new ArrayList<>();
        toRemove.add(entry);
        removeAll(KEY_ENTRIESLIST, toRemove);
    }
}

package com.example.comfortzone.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

    public int getLevel() throws ParseException {
        return fetchIfNeeded().getInt(KEY_LEVEL);
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
}

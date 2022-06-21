package com.example.comfortzone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("LevelsTracker")
public class LevelsTracker extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_COUNT = "count";

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

    public void addEntry(int level, ComfortLevelEntry entry) {
        increaseCount();

    }
}

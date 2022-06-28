package com.example.comfortzone.models;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TodayEntry")
public class TodayEntry extends ParseObject {

    public static final String KEY_TEMP = "temp";
    public static final String KEY_COMFORTLEVEL = "comfortLevel";
    public static final String KEY_USER = "user";
    public static final String KEY_COMFORT_LEVEL_ENTRY = "comfortLevelEntry";

    public TodayEntry() {};

    public TodayEntry(ParseUser user, int temp, int comfort, ComfortLevelEntry entry) {
        setUser(user);
        setTemp(temp);
        setComfortLevel(comfort);
        setComfortLevelEntry(entry);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setTemp(int temp) {
        put(KEY_TEMP, temp);
    };

    public int getTemp() {
        return getInt(KEY_TEMP);
    };

    public void setComfortLevel(int comfort) {
        put(KEY_COMFORTLEVEL, comfort);
    }

    public void setComfortLevelEntry(ComfortLevelEntry entry) {
        put(KEY_COMFORT_LEVEL_ENTRY, entry);
    }

    public ComfortLevelEntry getComfortLevelEntry() {
        return (ComfortLevelEntry) get(KEY_COMFORT_LEVEL_ENTRY);
    }

    public int getComfortLevel() {
        return getInt(KEY_COMFORTLEVEL);
    }

    public void deleteEntry() {
        deleteInBackground();
    }
}

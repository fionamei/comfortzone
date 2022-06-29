package com.example.comfortzone.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ComfortLevelEntry")
public class ComfortLevelEntry extends ParseObject {

    public static final String KEY_TEMP = "temp";
    public static final String KEY_COMFORTLEVEL = "comfortLevel";
    public static final String KEY_USER = "user";

    public ComfortLevelEntry() {};

    public ComfortLevelEntry(ParseUser user, int temp, int comfort) {
        setUser(user);
        setTemp(temp);
        setComfortLevel(comfort);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setTemp(int temp) {
        put(KEY_TEMP, temp);
    };

    public int getTemp() throws ParseException {
        return fetchIfNeeded().getInt(KEY_TEMP);
    };

    public void setComfortLevel(int comfort) {
        put(KEY_COMFORTLEVEL, comfort);
    }

    public int getComfortLevel() {
        return getInt(KEY_COMFORTLEVEL);
    }

}

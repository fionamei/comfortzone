package com.example.comfortzone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("LevelsTracker")
public class LevelsTracker extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_ZERO = "zero";
    public static final String KEY_FIVE = "five";
    public static final String KEY_TEN = "ten";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getZero() {
        return getInt(KEY_ZERO);
    }

    public void setZero(int level) {
        put(KEY_ZERO, level);
    }

    public int getFive() {
        return getInt(KEY_FIVE);
    }

    public void setFive(int level) {
        put(KEY_FIVE, level);
    }
    public int getTen() {
        return getInt(KEY_TEN);
    }

    public void setTen(int level) {
        put(KEY_TEN, level);
    }
}

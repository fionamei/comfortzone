package com.example.comfortzone.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.ArrayList;

@ParseClassName("LevelsTracker")
public class LevelsTracker extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_COUNT = "count";
    public static final String KEY_ENTRIESLIST = "entriesList";
    public static final String TAG = "LevelsTracker";
    public static final String KEY_AVERAGE = "average";

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

    public int getCount()  {
        try {
            return fetchIfNeeded().getInt(KEY_COUNT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return 0;
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
}

package com.example.comfortzone.Utils;

import android.util.Log;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.TodayEntry;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.List;

public class ParseUtil {

    public static final String TAG = "ParseUtil";
    public static final String KEY_TODAY_ENTRIES = "todayEntries";

    public static void updateEntriesList(ParseUser currentUser, int temp, int comfortLevel) {
        createTodayEntry(currentUser, temp, comfortLevel);
        ParseQuery<LevelsTracker> query = ParseQuery.getQuery("LevelsTracker");
        query.whereEqualTo("user", currentUser);
        query.whereEqualTo("level", comfortLevel);
        query.findInBackground(new FindCallback<LevelsTracker>() {
            @Override
            public void done(List<LevelsTracker> objects, ParseException e) {
                if (e != null || objects.isEmpty()) {
                    Log.e(TAG, "error finding tracker " + e);
                } else {
                    LevelsTracker tracker = objects.get(0);
                    ComfortLevelEntry newEntry = new ComfortLevelEntry(currentUser, temp, comfortLevel);
                    tracker.addEntry(newEntry);
                    tracker.increaseCount();
                    tracker.saveInBackground();
                }
            }
        });
    }

    public static void createTodayEntry (ParseUser currentUser, int temp, int comfortLevel) {
        TodayEntry newEntry = new TodayEntry(currentUser, temp, comfortLevel);
        newEntry.saveInBackground();
    }
}

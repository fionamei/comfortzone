package com.example.comfortzone.Utils;

import android.util.Log;

import com.example.comfortzone.TodayEntryCallback;
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

public static void updateEntriesList(ParseUser currentUser, int temp, int comfortLevel, ComfortLevelEntry newEntry) {

        ParseQuery<LevelsTracker> query = ParseQuery.getQuery("LevelsTracker");
        query.whereEqualTo(LevelsTracker.KEY_USER, currentUser);
        query.whereEqualTo(LevelsTracker.KEY_LEVEL, comfortLevel);
        query.findInBackground(new FindCallback<LevelsTracker>() {
            @Override
            public void done(List<LevelsTracker> objects, ParseException e) {
                if (e != null || objects.isEmpty()) {
                    Log.e(TAG, "error finding tracker " + e);
                } else {
                    LevelsTracker tracker = objects.get(0);
                    tracker.addEntry(newEntry);
                    try {
                        tracker.increaseCount();
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    tracker.saveInBackground();
                    newEntry.setLevelTracker(tracker);
                    newEntry.saveInBackground();
                }
            }
        });
    }

    public static void createTodayEntry (ParseUser currentUser, int temp, int comfortLevel, ComfortLevelEntry entry, TodayEntryCallback callback) {
        TodayEntry newEntry = new TodayEntry(currentUser, temp, comfortLevel, entry);
        newEntry.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                callback.todayEntry(newEntry);
            }
        });
    }

}


package com.example.comfortzone.utils;


import static com.example.comfortzone.utils.ComfortCalcUtil.calculateComfortTemp;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;


public class ComfortLevelUtil {

    public static final String TAG = "ComfortLevelUtil";
    public static final String KEY_TODAY_ENTRIES = "todayEntries";

    public static void updateEntriesList(ParseUser currentUser, ComfortLevelEntry newEntry) throws ParseException {

        ArrayList<LevelsTracker> levelTrackersArray = (ArrayList<LevelsTracker>) currentUser.get("levelTrackers");
        for (LevelsTracker tracker : levelTrackersArray) {
            if (tracker.getLevel() == newEntry.getComfortLevel()) {
                tracker.addEntry(newEntry);
                tracker.increaseCount();
                tracker.saveInBackground();

                newEntry.setLevelTracker(tracker);
                newEntry.saveInBackground();
            }
        }
        currentUser.add(KEY_TODAY_ENTRIES, newEntry);
        currentUser.saveInBackground();
    }

    public static void updateComfortLevel (ParseUser currentUser) {
        int newComfortLevel = calculateComfortTemp(currentUser);
        currentUser.put(ComfortCalcUtil.KEY_PERFECT_COMFORT, newComfortLevel);
        ArrayList<ComfortLevelEntry> entriesList = (ArrayList<ComfortLevelEntry>) currentUser.get(KEY_TODAY_ENTRIES);
        currentUser.removeAll(KEY_TODAY_ENTRIES , entriesList);
        currentUser.saveInBackground();
    }

}

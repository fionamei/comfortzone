package com.example.comfortzone.Utils;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ParseUtil {

    public static final String TAG = "ParseUtil";
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


}

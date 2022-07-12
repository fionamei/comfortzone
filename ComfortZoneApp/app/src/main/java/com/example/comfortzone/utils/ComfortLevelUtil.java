
package com.example.comfortzone.utils;


import static com.example.comfortzone.utils.ComfortCalcUtil.calculateComfortTemp;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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

    public static void sortTrackerByLevel(List<LevelsTracker> trackerList) {
        Collections.sort(trackerList, new Comparator<LevelsTracker>(){
            public int compare(LevelsTracker o1, LevelsTracker o2){
                int trackerlevel1 = o1.getLevel();
                int trackerlevel2 = o1.getLevel();
                if (trackerlevel1 == trackerlevel2) return 0;
                return trackerlevel1 < trackerlevel2 ? -1 : 1;
            }
        });
    }

}

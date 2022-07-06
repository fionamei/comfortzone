package com.example.comfortzone.Utils;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ComfortCalcUtil {
    public static final String KEY_PERFECT_COMFORT = "perfectComfort";
    public static final String TAG = "ComfortCalcUtil";
    public static final String KEY_LEVEL_TRACKERS = "levelTrackers";
    public static final double[] WEIGHTS = new double[]{0.1, 0.2, 0.35, 0.5, 1.0, 2.0, 1.0, 0.5, 0.35, 0.2, 0.1};

    /**
     * calculateComfortTemp: takes sum of (entries * weight / count) for each tracker level (from 0 - 11)
     * and divides it by the sum of weights of trackers that have more than one input
     **/
    public static int calculateComfortTemp(ParseUser currentUser) {
        ArrayList<LevelsTracker> trackerArrayList = (ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS);
        int sumWeight = trackerArrayList.stream().mapToInt(tracker -> levelWeightCalc(tracker)).sum();
        double sumDenom = trackerArrayList.stream().filter(tracker -> tracker.getCount() > 0).mapToDouble(tracker -> getWeight(tracker)).sum();
        if (sumDenom == 0) {
            return 0;
        }
        return (int) Math.round(sumWeight / sumDenom);
    };

    private static double getWeight(LevelsTracker tracker) {
        try {
            int level = tracker.getLevel();
            return WEIGHTS[level];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int levelWeightCalc(LevelsTracker tracker) {
        // returns the sum of entries * weight / count
        double weight = getWeight(tracker);
        int sum = getSum(tracker);
        int count = tracker.getCount();
        if (count > 0) {
            return (int) (weight * sum / count);
        }
        return 0;
    }

    private static int getSum(LevelsTracker tracker) {
        ArrayList<ComfortLevelEntry> entryArrayList = (ArrayList<ComfortLevelEntry>) tracker.get(LevelsTracker.KEY_ENTRIESLIST);
        return entryArrayList.stream().mapToInt(entry -> getTemp(entry)).sum();
    }

    private static int getTemp(ComfortLevelEntry entry) {
        try {
            return entry.getTemp();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

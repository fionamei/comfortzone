package com.example.comfortzone.Utils;

import android.util.Log;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ComfortCalcUtil {
    public static final String KEY_PERFECT_COMFORT = "perfectComfort";
    public static final String TAG = "ComfortCalcUtil";
    public static final String KEY_LEVEL_TRACKERS = "levelTrackers";
    public static final double LEVEL_ZERO_WEIGHT = 0.1;
    public static final double LEVEL_ONE_WEIGHT = 0.2;
    public static final double LEVEL_TWO_WEIGHT = 0.35;
    public static final double LEVEL_THREE_WEIGHT = 0.5;
    public static final double LEVEL_FOUR_WEIGHT = 1.0;
    public static final double LEVEL_FIVE_WEIGHT = 2.0;
    public static final double LEVEL_SIX_WEIGHT = 1.0;
    public static final double LEVEL_SEVEN_WEIGHT = 0.5;
    public static final double LEVEL_EIGHT_WEIGHT = 0.35;
    public static final double LEVEL_NINE_WEIGHT = 0.2;
    public static final double LEVEL_TEN_WEIGHT = 0.1;
    public static final int LEVEL_ZERO = 0;
    public static final int LEVEL_ONE = 1;
    public static final int LEVEL_TWO = 2;
    public static final int LEVEL_THREE = 3;
    public static final int LEVEL_FOUR = 4;
    public static final int LEVEL_FIVE = 5;
    public static final int LEVEL_SIX = 6;
    public static final int LEVEL_SEVEN = 7;
    public static final int LEVEL_EIGHT = 8;
    public static final int LEVEL_NINE = 9;
    public static final int LEVEL_TEN = 10;

    public static int initialComfortCalculator(int tempZero, int tempFive, int tempTen) {
        double zeroWeight = tempZero * LEVEL_ZERO_WEIGHT;
        double fiveWeight = tempFive * LEVEL_FIVE_WEIGHT;
        double tenWeight = tempTen * LEVEL_TEN_WEIGHT;
        double sum = zeroWeight + fiveWeight + tenWeight;
        double denom_sum = LEVEL_ZERO_WEIGHT + LEVEL_FIVE_WEIGHT + LEVEL_TEN_WEIGHT;
        return (int) (sum / denom_sum);
    }

    public static int generalCalc(ParseUser currentUser) throws ParseException {
        ArrayList<LevelsTracker> trackerArrayList = (ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS);
        int sumWeight = trackerArrayList.stream().mapToInt(tracker -> levelWeightCalc(tracker)).sum();
        double sumDenom = trackerArrayList.stream().filter(tracker -> tracker.getCount() > 0).mapToDouble(tracker -> tryCatchLevelWeight(tracker)).sum();
        return (int) (sumWeight / sumDenom);
    };

    private static double tryCatchLevelWeight(LevelsTracker tracker) {
        try {
            return levelWeight(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int levelWeightCalc(LevelsTracker tracker) {
        // returns the sum of entries * weight / count
        double weight = 0;
        int sum = 0;
        try {
            weight = levelWeight(tracker);
            sum = getSum(tracker);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) (weight * sum);
    }

    private static double levelWeight(LevelsTracker tracker) throws ParseException {
        int level = tracker.getLevel();
        if (level == LEVEL_ZERO) {
            return LEVEL_ZERO_WEIGHT;
        } else if (level == LEVEL_ONE) {
            return LEVEL_ONE_WEIGHT;
        } else if (level == LEVEL_TWO) {
            return LEVEL_TWO_WEIGHT;
        } else if (level == LEVEL_THREE) {
            return LEVEL_THREE_WEIGHT;
        } else if (level == LEVEL_FOUR) {
            return LEVEL_FOUR_WEIGHT;
        } else if (level == LEVEL_FIVE) {
            return LEVEL_FIVE_WEIGHT;
        } else if (level == LEVEL_SIX) {
            return LEVEL_SIX_WEIGHT;
        } else if (level == LEVEL_SEVEN) {
            return LEVEL_SEVEN_WEIGHT;
        } else if (level == LEVEL_EIGHT) {
            return LEVEL_EIGHT_WEIGHT;
        } else if (level == LEVEL_NINE) {
            return LEVEL_NINE_WEIGHT;
        } else {
            return LEVEL_TEN_WEIGHT;
        }
    }

    private static int getSum(LevelsTracker tracker) {
        ArrayList<ComfortLevelEntry> entryArrayList = (ArrayList<ComfortLevelEntry>) tracker.get(LevelsTracker.KEY_ENTRIESLIST);
        int sum = entryArrayList.stream().mapToInt(entry -> getTemp(entry)).sum();
        return sum;
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

package com.example.comfortzone.utils;

import static com.example.comfortzone.InitialComfortActivity.TOTAL_LEVELS;
import static com.example.comfortzone.models.LevelsTracker.KEY_AVERAGE;
import static com.example.comfortzone.models.LevelsTracker.KEY_ENTRIESLIST;

import android.util.Log;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.google.common.collect.Ordering;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComfortCalcUtil {
    public static final String KEY_PERFECT_COMFORT = "perfectComfort";
    public static final String TAG = "ComfortCalcUtil";
    public static final String KEY_LEVEL_TRACKERS = "levelTrackers";
    public static final String KEY_TEMP_AVERAGE = "tempAverage";
    public static final String KEY_LOW_RANGE = "lowRange";
    public static final String KEY_HIGH_RANGE = "highRange";
    public static final double[] WEIGHTS = new double[]{0.1, 0.2, 0.35, 0.5, 1.0, 2.0, 1.0, 0.5, 0.35, 0.2, 0.1};
    public static final int MIN_TEMP = -999;
    public static final int MAX_TEMP = 999;
    public static final int INTERVAL_SCALE = 5;
    private static int[] averages = new int[TOTAL_LEVELS];


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
        ArrayList<ComfortLevelEntry> entryArrayList = (ArrayList<ComfortLevelEntry>) tracker.get(KEY_ENTRIESLIST);
        return entryArrayList.stream().mapToInt(entry -> entry.getTemp()).sum();
    }

    public static void calculateAverages(ParseUser currentUser) {
        ArrayList<LevelsTracker> trackerArrayList = (ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS);
        List<Task<Void>> savingAverages = trackerArrayList.stream().map(tracker -> calculateSingularAverage(tracker)).collect(Collectors.toList());
        Task.whenAll(savingAverages).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                goThroughTrackerList(trackerArrayList);
                findBlanks();
                Task.whenAll(saveTempAverage(trackerArrayList)).onSuccess(new Continuation<Void, Object>() {
                    @Override
                    public Object then(Task<Void> task) throws Exception {
                        getAndSaveRanges(trackerArrayList);
                        return null;
                    }
                });
                return null;
            }
        });
    }

    private static Task<Void> calculateSingularAverage(LevelsTracker tracker) {
        ArrayList<ComfortLevelEntry> entryArrayList = null;
        try {
            entryArrayList = (ArrayList<ComfortLevelEntry>) tracker.fetchIfNeeded().get(KEY_ENTRIESLIST);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int sum = entryArrayList.stream().mapToInt(entry -> entry.getTemp()).sum();
        int count = tracker.getCount();
        if (count > 0) {
            tracker.put(KEY_AVERAGE, sum / count);
        } else {
            tracker.put(KEY_AVERAGE, MIN_TEMP);
        }
        return tracker.saveInBackground();
    }

    private static void goThroughTrackerList(List<LevelsTracker> trackerList) {
        int firstNonEmptyVal = 1;
        while (trackerList.get(firstNonEmptyVal).getAverage() == MIN_TEMP) {
            averages[firstNonEmptyVal] = MIN_TEMP;
            firstNonEmptyVal += 1;
        }
        if (trackerList.get(0).getAverage() < trackerList.get(firstNonEmptyVal).getAverage() || trackerList.get(0).getCount() > trackerList.get(1).getCount()) {
            averages[0] = trackerList.get(0).getAverage();
        } else {
            averages[0] = MIN_TEMP;
        }

        int earlierIndex = firstNonEmptyVal - 1;
        for (int i = firstNonEmptyVal; i < TOTAL_LEVELS; i++) {
            LevelsTracker earlierTracker = trackerList.get(earlierIndex);
            LevelsTracker laterTracker = trackerList.get(i);
            if (laterTracker.getAverage() != MIN_TEMP) {

                if (earlierTracker.getAverage() > laterTracker.getAverage()) {
                    int countEarlier = earlierTracker.getCount();
                    int countLater = laterTracker.getCount();

                    if (countEarlier > countLater) {
                        ArrayList<ComfortLevelEntry> entryArrayList = (ArrayList<ComfortLevelEntry>) laterTracker.get(KEY_ENTRIESLIST);
                        List<Integer> newEntries = entryArrayList.stream().filter(entry -> entry.getTemp() > earlierTracker.getAverage()).map(entry -> entry.getTemp()).collect(Collectors.toList());
                        int sum = newEntries.stream().mapToInt(Integer::intValue).sum();
                        if (newEntries.size() > 0) {
                            averages[i] = sum / newEntries.size();
                        } else {
                            averages[i] = MIN_TEMP;
                        }

                    } else {
                        averages[i] = laterTracker.getAverage();
                        ArrayList<ComfortLevelEntry> entryArrayList = (ArrayList<ComfortLevelEntry>) earlierTracker.get(KEY_ENTRIESLIST);
                        int upperbound = laterTracker.getAverage();
                        filterLowerTemps(entryArrayList, upperbound, earlierIndex);

                        int currentPointer = i;
                        for (int backwardsPointer = earlierIndex - 1; backwardsPointer >= 0; backwardsPointer--) {
                            if (averages[backwardsPointer] > averages[currentPointer]) {
                                LevelsTracker currentTracker = trackerList.get(backwardsPointer);
                                ArrayList<ComfortLevelEntry> currentEntries = (ArrayList<ComfortLevelEntry>) currentTracker.get(KEY_ENTRIESLIST);
                                filterLowerTemps(currentEntries, trackerList.get(currentPointer).getAverage(), backwardsPointer);

                                if (averages[backwardsPointer] != MIN_TEMP) {
                                    currentPointer = backwardsPointer;
                                }
                            } else if (averages[backwardsPointer] != MIN_TEMP) {
                                break;
                            }
                        }
                        earlierIndex = i;
                    }
                } else {
                    earlierIndex = i;
                    averages[i] = laterTracker.getAverage();
                }
            } else {
                averages[i] = MIN_TEMP;
            }
        }
    }

    private static void filterLowerTemps(ArrayList<ComfortLevelEntry> entryArrayList, int upperbound, int position) {
        List<Integer> newEntries = entryArrayList.stream().filter(entry -> entry.getTemp() < upperbound).map(entry -> entry.getTemp()).collect(Collectors.toList());
        int sum = newEntries.stream().mapToInt(Integer::intValue).sum();
        if (newEntries.size() > 0) {
            averages[position] = sum / newEntries.size();
        } else {
            averages[position] = MIN_TEMP;
        }
    }

    private static void findBlanks() {
        int countBlanks = 0;
        int lowerIndex = 0;
        int higherIndex = 0;
        while (averages[higherIndex] == MIN_TEMP) {
            higherIndex++;
        }

        if (higherIndex != lowerIndex) {
            averages[lowerIndex] = averages[higherIndex] - (INTERVAL_SCALE * (higherIndex - lowerIndex));
            fillInTheBlanks(lowerIndex, higherIndex);
            lowerIndex = higherIndex;
        }

        for (int i = lowerIndex; i < TOTAL_LEVELS; i ++) {
            if (averages[i] == MIN_TEMP) {
                countBlanks++;
            } else {
                if (countBlanks > 0) {
                    higherIndex = i;

                    fillInTheBlanks(lowerIndex, higherIndex);
                    lowerIndex = higherIndex;

                } else {
                    lowerIndex = i;
                }
            }
        }
    }

    private static void fillInTheBlanks(int lowerIndex, int higherIndex) {
        int indexDiff = higherIndex - lowerIndex;
        int valueDiff = averages[higherIndex] - averages[lowerIndex];
        int interval = valueDiff / indexDiff;
        int valueToAdd = averages[lowerIndex];
        for (int i = lowerIndex + 1; i < higherIndex; i++) {
            valueToAdd = valueToAdd + interval;
            averages[i] = valueToAdd;
        }
    }

    private static List<Task<Void>> saveTempAverage(ArrayList<LevelsTracker> trackerArrayList) {
        List<Task<Void>> tasksList = new ArrayList<>();
         for (int i = 0; i < TOTAL_LEVELS; i++) {
             LevelsTracker tracker = trackerArrayList.get(i);
             int average = averages[i];
             tracker.put(KEY_TEMP_AVERAGE, average);
             tasksList.add(tracker.saveInBackground());
         }
         return tasksList;
    }

    private static void getAndSaveRanges(ArrayList<LevelsTracker> trackerArrayList) {
        int minTemp = MIN_TEMP;
        for (int i = 0; i < TOTAL_LEVELS - 1; i++) {
            int lowRange = trackerArrayList.get(i).getTempAverage();
            int highRange = trackerArrayList.get(i + 1).getTempAverage();
            int highTemp = (highRange + lowRange) / 2;
            LevelsTracker tracker = trackerArrayList.get(i);
            tracker.put(KEY_LOW_RANGE, minTemp);
            tracker.put(KEY_HIGH_RANGE, highTemp);
            minTemp = highTemp;
            tracker.saveInBackground();
        }
        LevelsTracker lastTracker = trackerArrayList.get(TOTAL_LEVELS - 1);
        lastTracker.put(KEY_LOW_RANGE, minTemp);
        lastTracker.put(KEY_HIGH_RANGE, MAX_TEMP);
        lastTracker.saveInBackground();
    }
}

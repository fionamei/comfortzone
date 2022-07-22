package com.example.comfortzone.utils;

import static com.example.comfortzone.initial.ui.InitialComfortActivity.TOTAL_LEVELS;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.ParseUser;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComfortCalcUtil {
    public static final String KEY_PERFECT_COMFORT = "perfectComfort";
    public static final String TAG = "ComfortCalcUtil";
    public static final String KEY_LEVEL_TRACKERS = "levelTrackers";
    public static final String KEY_TEMP_AVERAGE = "tempAverage";
    public static final double[] WEIGHTS = new double[]{0.1, 0.2, 0.35, 0.5, 1.0, 2.0, 1.0, 0.5, 0.35, 0.2, 0.1};
    public static final int MIN_TEMP = -999;
    public static final int MAX_TEMP = 999;
    public static final int INTERVAL_SCALE = 5;


    /**
     * calculateComfortTemp: takes sum of (entries * weight / count) for each tracker level (from 0 - 11)
     * and divides it by the sum of weights of trackers that have more than one input
     **/
    public static int calculateComfortTemp(ParseUser currentUser) {
        ArrayList<LevelsTracker> trackerArrayList = (ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS);
        int sumWeight = trackerArrayList.stream().mapToInt(tracker -> levelWeightCalc(tracker)).sum();
        double sumDenom = trackerArrayList.stream().filter(tracker -> tracker.getCount() > 0)
                .mapToDouble(tracker -> getWeight(tracker)).sum();
        if (sumDenom == 0) {
            return 0;
        }
        return (int) Math.round(sumWeight / sumDenom);
    };

    private static double getWeight(LevelsTracker tracker) {
        int level = tracker.getLevel();
        return WEIGHTS[level];
    }

    /**
     * returns sum of entries * weight / count for one level
     */
    private static int levelWeightCalc(LevelsTracker tracker) {
        double weight = getWeight(tracker);
        int sum = getSum(tracker);
        int count = tracker.getCount();
        if (count > 0) {
            return (int) (weight * sum / count);
        }
        return 0;
    }

    private static int getSum(LevelsTracker tracker) {
        ArrayList<ComfortLevelEntry> entryArrayList = tracker.getComfortEntriesList();
        return entryArrayList.stream().mapToInt(entry -> entry.getTemp()).sum();
    }

    public static void calculateAverages(ParseUser currentUser) {
        ArrayList<LevelsTracker> trackerArrayList = (ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS);
        List<Task<Void>> savingAverages = trackerArrayList.stream()
                .map(tracker -> calculateSingularAverage(tracker))
                .collect(Collectors.toList());
        Task.whenAll(savingAverages).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                int[] averages = new int[TOTAL_LEVELS];
                goThroughTrackerList(trackerArrayList, averages);
                fillInEmptyAverages(averages);
                saveTempAverageAndRanges(trackerArrayList, averages);
                return null;
            }
        });
    }

    private static Task<Void> calculateSingularAverage(LevelsTracker tracker) {
        List<ComfortLevelEntry> entryArrayList = tracker.getComfortEntriesList();
        List<Integer> newEntries = entryArrayList.stream().map(entry -> entry.getTemp()).collect(Collectors.toList());
        tracker.setAverage(getAverage(newEntries, MIN_TEMP));
        return tracker.saveInBackground();
    }

    /**
     * goThroughTrackerList: goes through list of trackers to make sure their averages are in ascending order,
     * if two averages are not in ascending order, find the value which is more "valid" based on more inputs,
     * and set the other average by filtering only valid temperatures
     *
     * Between the two values, if the earlier value is changed, algorithm will go back to check if previous values
     * are still valid.
     **/
    private static void goThroughTrackerList(List<LevelsTracker> trackerList, int[] averages) {
        int firstNonEmptyVal = 1;
        while (trackerList.get(firstNonEmptyVal).getAverage() == MIN_TEMP) {
            averages[firstNonEmptyVal] = MIN_TEMP;
            firstNonEmptyVal += 1;
        }
        if (isValid(trackerList.get(0), trackerList.get(firstNonEmptyVal))
                || trackerList.get(0).getCount() > trackerList.get(firstNonEmptyVal).getCount()) {
            averages[0] = trackerList.get(0).getAverage();
        } else {
            averages[0] = MIN_TEMP;
        }

        int earlierIndex = firstNonEmptyVal - 1;
        for (int i = firstNonEmptyVal; i < TOTAL_LEVELS; i++) {
            LevelsTracker earlierTracker = trackerList.get(earlierIndex);
            LevelsTracker laterTracker = trackerList.get(i);
            if (laterTracker.getAverage() != MIN_TEMP) {

                if (!isValid(earlierTracker, laterTracker)) {
                    int countEarlier = earlierTracker.getCount();
                    int countLater = laterTracker.getCount();

                    if (countEarlier >= countLater) {
                        ArrayList<ComfortLevelEntry> entryArrayList = laterTracker.getComfortEntriesList();
                        filterForHigherTemps(entryArrayList, earlierTracker.getAverage(), i, averages);

                    } else {
                        averages[i] = laterTracker.getAverage();
                        ArrayList<ComfortLevelEntry> entryArrayList = earlierTracker.getComfortEntriesList();
                        filterForLowerTemps(entryArrayList, laterTracker.getAverage(), earlierIndex, averages);
                        backtrackCheckPreviousAverages(trackerList, averages, earlierIndex, i);
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

    private static boolean isValid(LevelsTracker earlierTracker, LevelsTracker laterTracker) {
        return earlierTracker.getAverage() < laterTracker.getAverage();
    }

    private static void filterForHigherTemps(ArrayList<ComfortLevelEntry> entryArrayList, int lowerbound, int position, int[] averages) {
        List<Integer> newEntries = entryArrayList.stream()
                .filter(entry -> entry.getTemp() > lowerbound)
                .map(entry -> entry.getTemp())
                .collect(Collectors.toList());
        averages[position] = getAverage(newEntries, MIN_TEMP);
    }

    private static void filterForLowerTemps(ArrayList<ComfortLevelEntry> entryArrayList, int upperbound, int position, int[] averages) {
        List<Integer> newEntries = entryArrayList.stream()
                .filter(entry -> entry.getTemp() < upperbound)
                .map(entry -> entry.getTemp())
                .collect(Collectors.toList());
        averages[position] = getAverage(newEntries, MIN_TEMP);
    }

    private static int getAverage(List<Integer> newEntries, int defaultValue) {
        if (newEntries.size() > 0) {
            int sum = newEntries.stream().mapToInt(Integer::intValue).sum();
            return sum / newEntries.size();
        } else {
            return defaultValue;
        }
    }

    /**
     * backtrackCheckPreviousAverages: checks if previously changed averages are still in ascending order,
     * and if they are not, change them.
     * */
    private static void backtrackCheckPreviousAverages(List<LevelsTracker> trackerList, int[] averages, int earlierIndex, int i) {
        int currentPointer = i;
        for (int backwardsPointer = earlierIndex - 1; backwardsPointer >= 0; backwardsPointer--) {
            if (averages[backwardsPointer] > averages[currentPointer]) {
                LevelsTracker currentTracker = trackerList.get(backwardsPointer);
                ArrayList<ComfortLevelEntry> currentEntries = currentTracker.getComfortEntriesList();
                filterForLowerTemps(currentEntries, trackerList.get(currentPointer).getAverage(), backwardsPointer, averages);

                if (averages[backwardsPointer] != MIN_TEMP) {
                    currentPointer = backwardsPointer;
                }
            } else if (averages[backwardsPointer] != MIN_TEMP) {
                break;
            }
        }
    }

    private static void fillInEmptyAverages(int[] averages) {
        int countBlanks = 0;
        int lowerIndex = 0;
        int higherIndex = 0;
        while (averages[higherIndex] == MIN_TEMP) {
            higherIndex++;
        }

        if (higherIndex != lowerIndex) {
            averages[lowerIndex] = averages[higherIndex] - (INTERVAL_SCALE * (higherIndex - lowerIndex));
            fillInEmptyAveragesHelper(lowerIndex, higherIndex, averages);
            lowerIndex = higherIndex;
        }

        for (int i = lowerIndex; i < TOTAL_LEVELS; i ++) {
            if (averages[i] == MIN_TEMP) {
                countBlanks++;
            } else {
                if (countBlanks > 0) {
                    higherIndex = i;

                    fillInEmptyAveragesHelper(lowerIndex, higherIndex, averages);
                    lowerIndex = higherIndex;

                } else {
                    lowerIndex = i;
                }
            }
        }
    }

    private static void fillInEmptyAveragesHelper(int lowerIndex, int higherIndex, int[] averages) {
        int indexDiff = higherIndex - lowerIndex;
        int valueDiff = averages[higherIndex] - averages[lowerIndex];
        int interval = valueDiff / indexDiff;
        int valueToAdd = averages[lowerIndex];
        for (int i = lowerIndex + 1; i < higherIndex; i++) {
            valueToAdd = valueToAdd + interval;
            averages[i] = valueToAdd;
        }
    }

    private static void saveTempAverageAndRanges(ArrayList<LevelsTracker> trackerArrayList, int[] averages) {
        int minTemp = MIN_TEMP;
        for (int i = 0; i < TOTAL_LEVELS - 1; i++) {
            LevelsTracker tracker = trackerArrayList.get(i);
            int lowRange = averages[i];
            int highRange = averages[i+1];
            int highTemp = (highRange + lowRange) / 2;
            tracker.setLowRange(minTemp);
            tracker.setHighRange(highTemp);
            tracker.setTempAverage(averages[i]);
            minTemp = highTemp;
            saveTracker(tracker);
        }
        LevelsTracker lastTracker = trackerArrayList.get(TOTAL_LEVELS - 1);
        lastTracker.setLowRange(minTemp);
        lastTracker.setHighRange(MAX_TEMP);
        lastTracker.setTempAverage(averages[TOTAL_LEVELS - 1]);
        lastTracker.saveInBackground();
    }

    private static void saveTracker(LevelsTracker tracker) {
        tracker.saveInBackground();
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

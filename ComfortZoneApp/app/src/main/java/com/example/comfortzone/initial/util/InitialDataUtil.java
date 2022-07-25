package com.example.comfortzone.initial.util;

import static com.example.comfortzone.initial.ui.InitialComfortActivity.COLD_COMFORT_LEVEL;
import static com.example.comfortzone.initial.ui.InitialComfortActivity.HOT_COMFORT_LEVEL;
import static com.example.comfortzone.initial.ui.InitialComfortActivity.PERFECT_COMFORT_LEVEL;
import static com.example.comfortzone.initial.ui.InitialComfortActivity.TOTAL_LEVELS;
import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;

import android.util.Log;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.utils.ComfortCalcUtil;
import com.example.comfortzone.utils.ComfortLevelUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class InitialDataUtil {

    public static final String TAG = "InitialDataUtil";

    public static void signUp(String username, String password, SignUpCallback callback) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(callback);
    }

    public static void saveInitialLevels(ParseUser user, int tempZero, int tempFive, int tempTen) {
        ComfortLevelEntry entryZero = new ComfortLevelEntry(user, tempZero, COLD_COMFORT_LEVEL);;
        ComfortLevelEntry entryFive = new ComfortLevelEntry(user, tempFive, PERFECT_COMFORT_LEVEL);;
        ComfortLevelEntry entryTen = new ComfortLevelEntry(user, tempTen, HOT_COMFORT_LEVEL);;
        List<LevelsTracker> trackerList = new ArrayList<>();

        Task.whenAll(Arrays.asList(entryZero.saveInBackground(),
                entryFive.saveInBackground(), entryTen.saveInBackground())).onSuccess(
                new Continuation<Void, Object>() {
                    @Override
                    public Object then(Task<Void> task) throws Exception {
                        if (task.getError() != null) {
                            Log.e(TAG, "error saving entries in background");
                            return task.getError();
                        } else {
                            Collection<Task<Void>> levels = createLevels(user, trackerList, entryZero, entryFive, entryTen);
                            Task.whenAll(levels).onSuccess(new Continuation<Void, Object>() {
                                @Override
                                public Object then(Task<Void> task) throws Exception {
                                    if (task.getError() != null) {
                                        Log.e(TAG, "error saving entries in background");
                                    } else {
                                        ComfortLevelUtil.sortTrackerByLevel(trackerList);
                                        user.addAll(KEY_LEVEL_TRACKERS, trackerList);
                                        user.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                calculateComfort(user);
                                                ComfortCalcUtil.calculateAverages(user);
                                            }
                                        });
                                    }
                                    return null;
                                }
                            });
                            return null;
                        }
                    }
                });
    }

    public static List<Task<Void>> createLevels(ParseUser user, List<LevelsTracker> trackerList, ComfortLevelEntry entryZero,
                                                ComfortLevelEntry entryFive, ComfortLevelEntry entryTen) {
        List<Task<Void>> levels = new ArrayList<>();
        for (int i = 0; i < TOTAL_LEVELS; i ++) {
            levels.add(createLevel(i, user, trackerList, entryZero, entryFive, entryTen));
        }
        return levels;
    }

    public static Task<Void> createLevel(int level, ParseUser user, List<LevelsTracker> trackerList,
                                         ComfortLevelEntry entryZero, ComfortLevelEntry entryFive, ComfortLevelEntry entryTen) {
        LevelsTracker tracker = new LevelsTracker();
        tracker.setLevel(level);
        tracker.setUser(user);
        if (level == COLD_COMFORT_LEVEL) {
            tracker.addEntry(entryZero);
            tracker.increaseCount();
        } else if (level == PERFECT_COMFORT_LEVEL) {
            tracker.addEntry(entryFive);
            tracker.increaseCount();
        } else if (level == HOT_COMFORT_LEVEL) {
            tracker.addEntry(entryTen);
            tracker.increaseCount();
        }
        trackerList.add(tracker);
        return tracker.saveInBackground();
    }


    private static void calculateComfort(ParseUser user) {
        int comfort = ComfortCalcUtil.calculateComfortTemp(user);
        user.put(ComfortCalcUtil.KEY_PERFECT_COMFORT, comfort);
        user.saveInBackground();
    }
}

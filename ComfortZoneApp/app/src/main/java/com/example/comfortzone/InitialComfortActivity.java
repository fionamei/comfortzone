package com.example.comfortzone;

import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;
import static com.example.comfortzone.utils.ComfortCalcUtil.calculateComfortTemp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comfortzone.utils.ComfortCalcUtil;
import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InitialComfortActivity extends AppCompatActivity {

    public static final String TAG = "InitialComfortActivity";
    public static final int TOTAL_LEVELS = 11;
    public static final int COLD_COMFORT_LEVEL = 0;
    public static final int PERFECT_COMFORT_LEVEL = 5;
    public static final int HOT_COMFORT_LEVEL = 10;

    private EditText etZero;
    private EditText etFive;
    private EditText etTen;
    private Button btnConfirm;
    private ParseUser user;
    private ComfortLevelEntry entryZero;
    private ComfortLevelEntry entryFive;
    private ComfortLevelEntry entryTen;
    private List<LevelsTracker> trackerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_comfort_actvitiy);

        user = ParseUser.getCurrentUser();
        trackerList = new ArrayList<>();

        initViews();
        confirmListener();
    }

    private void initViews() {
        etZero = findViewById(R.id.etZero);
        etFive = findViewById(R.id.etFive);
        etTen = findViewById(R.id.etTen);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void confirmListener() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempZero = Integer.parseInt(etZero.getText().toString());
                int tempFive = Integer.parseInt(etFive.getText().toString());
                int tempTen = Integer.parseInt(etTen.getText().toString());
                save(tempZero, tempFive, tempTen);
                goHostActivity();
            }
        });
    }

    private void calculateComfort(ParseUser user) {
        int comfort = calculateComfortTemp(user);
        user.put(ComfortCalcUtil.KEY_PERFECT_COMFORT, comfort);
        user.saveInBackground();
    }

    private void save(int tempZero, int tempFive, int tempTen) {
        entryZero = new ComfortLevelEntry(user, tempZero, COLD_COMFORT_LEVEL);
        entryFive = new ComfortLevelEntry(user, tempFive, PERFECT_COMFORT_LEVEL);
        entryTen = new ComfortLevelEntry(user, tempTen, HOT_COMFORT_LEVEL);

        Task.whenAll(Arrays.asList(entryZero.saveInBackground(),
                entryFive.saveInBackground(), entryTen.saveInBackground())).onSuccess(
                new Continuation<Void, Object>() {
                    @Override
                    public Object then(Task<Void> task) throws Exception {
                        if (task.getError() != null) {
                            Log.e(TAG, "error saving entries in background");
                            return task.getError();
                        } else {
                            Collection<Task<Void>> levels = createLevels();
                            Task.whenAll(levels).onSuccess(new Continuation<Void, Object>() {
                                @Override
                                public Object then(Task<Void> task) throws Exception {
                                    if (task.getError() != null) {
                                        Log.e(TAG, "error saving entries in background");
                                    } else {
                                        sortTrackerByLevel();
                                        user.addAll(KEY_LEVEL_TRACKERS, trackerList);
                                        user.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                calculateComfort(user);
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


    public List<Task<Void>> createLevels() {
        List<Task<Void>> levels = new ArrayList<>();
        for (int i = 0; i < TOTAL_LEVELS; i ++) {
            levels.add(createLevel(i));
        }
        return levels;
    }

    public Task<Void> createLevel(int level) {
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

    private void sortTrackerByLevel() {
        Collections.sort(trackerList, new Comparator<LevelsTracker>(){
            public int compare(LevelsTracker o1, LevelsTracker o2){
                int trackerlevel1 = -1;
                int trackerlevel2 = -1;
                try {
                    trackerlevel1 = o1.getLevel();
                    trackerlevel2 = o1.getLevel();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(trackerlevel1 == trackerlevel2)
                    return 0;
                return trackerlevel1 < trackerlevel2 ? -1 : 1;
            }
        });
    }

    private void goHostActivity() {
        Intent i = new Intent(this, HostActivity.class);
        startActivity(i);
        finish();
    }

}
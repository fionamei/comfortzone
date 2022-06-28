package com.example.comfortzone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.SaveCallback;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;


import java.util.Arrays;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_comfort_actvitiy);

        user = ParseUser.getCurrentUser();

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
                            createLevels();
                            return null;
                        }
                    }
                });

    }

    public void createLevels() throws ParseException {
        for (int i = 0; i < TOTAL_LEVELS; i ++) {
            createLevel(i);
        }
    }

    public void createLevel(int level) throws ParseException {
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
        tracker.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error creating levels" + e);
                } else {
                    user.add("levelTrackers", tracker);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "error saving tracker to user" + e);
                            }
                        }
                    });
                }
            }
        });
    }

    private void goHostActivity() {
        Intent i = new Intent(this, HostActivity.class);
        startActivity(i);
        finish();
    }

}
package com.example.comfortzone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.SaveCallback;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;


import java.util.Arrays;
import java.util.List;

public class InitialComfortActivity extends AppCompatActivity {

    public static final String TAG = "InitialComfortActivity";
    private EditText etZero;
    private EditText etFive;
    private EditText etTen;
    private Button btnConfirm;
    private ParseUser user;
    ComfortLevelEntry entryZero;
    ComfortLevelEntry entryFive;
    ComfortLevelEntry entryTen;

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
                goMainActivity();
            }
        });
    }

    private void save(int tempZero, int tempFive, int tempTen) {

        entryZero = new ComfortLevelEntry(user, tempZero, 0);
        entryFive = new ComfortLevelEntry(user, tempFive, 5);
        entryTen = new ComfortLevelEntry(user, tempTen, 10);

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

    public void createLevels() {
        int totalLevels = 11;
        for (int i = 0; i < totalLevels; i ++) {
            createLevel(i);
        }
    }

    public void createLevel(int level) {
        LevelsTracker tracker = new LevelsTracker();
        tracker.setLevel(level);
        tracker.setUser(user);
        tracker.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error creating levels" + e);
                } else {

                    if (level == 0) {
                        addEntry(entryZero);
                    } else if (level == 5) {
                        addEntry(entryFive);
                    } else if (level == 10) {
                        addEntry(entryTen);
                    }

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

    public void addEntry(ComfortLevelEntry entry) {
        ParseQuery<LevelsTracker> query = ParseQuery.getQuery("LevelsTracker");
        query.whereEqualTo("user", user);
        query.whereEqualTo("level", entry.getComfortLevel());
        query.findInBackground(new FindCallback<LevelsTracker>() {
            @Override
            public void done(List<LevelsTracker> objects, ParseException e) {
                if (e != null || objects.isEmpty()) {
                    Log.e(TAG, "error adding entries to tracker " + objects.toString() + " ParseException is " + e);
                } else {
                    LevelsTracker tracker = objects.get(0);
                    tracker.add(LevelsTracker.KEY_ENTRIESLIST, entry);
                    tracker.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "error adding entry to tracker");
                            }
                        }
                    });
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
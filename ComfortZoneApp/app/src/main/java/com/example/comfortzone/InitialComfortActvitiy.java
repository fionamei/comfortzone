package com.example.comfortzone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InitialComfortActvitiy extends AppCompatActivity {

    public static final String TAG = "InitialComfortActivity";
    private EditText etZero;
    private EditText etFive;
    private EditText etTen;
    private Button btnConfirm;
    private ParseUser user;
    private AsyncProcessesCallback callback;
    ComfortLevelEntry entryZero;
    ComfortLevelEntry entryFive;
    ComfortLevelEntry entryTen;
    AtomicInteger comfortCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_comfort_actvitiy);

        comfortCounter = new AtomicInteger(0);
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
                int tempzero = Integer.parseInt(etZero.getText().toString());
                int tempfive = Integer.parseInt(etFive.getText().toString());
                int tempten = Integer.parseInt(etTen.getText().toString());
                save(tempzero, tempfive, tempten);
                goMainActivity();
            }
        });
    }

    private void save(int tempzero, int tempfive, int tempten) {
        settingUp(tempzero, tempfive, tempten, new AsyncProcessesCallback() {
            @Override
            public void onAsyncSuccess() {
                addEntry(entryZero);
                addEntry(entryFive);
                addEntry(entryTen);
            }
        });



    }

    private void settingUp(int tempzero, int tempfive, int tempten, AsyncProcessesCallback callback) {
        createLevels(); // creates the empty 0-10 comfort level scale
        setInitialEntries(tempzero, tempfive, tempten);

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

    private void setInitialEntries(int tempzero, int tempfive, int tempten) {
        entryZero = new ComfortLevelEntry(user, tempzero, 0);
        entryFive = new ComfortLevelEntry(user, tempfive, 5);
        entryTen = new ComfortLevelEntry(user, tempten, 10);

        saveComfortLevel(entryZero);
        saveComfortLevel(entryFive);
        saveComfortLevel(entryTen);
    }

    public void saveComfortLevel(ComfortLevelEntry entry) {
        entry.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e!= null) {
                    Log.e(TAG, "error while saving" + e);
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
                    Log.e(TAG, "error adding entries to tracker" + e);
                } else {
                    LevelsTracker tracker = objects.get(0);
                    tracker.add(LevelsTracker.KEY_ENTRIESLIST, entry);
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
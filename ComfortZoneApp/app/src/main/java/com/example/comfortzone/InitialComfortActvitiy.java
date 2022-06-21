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
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.SaveCallback;

public class InitialComfortActvitiy extends AppCompatActivity {

    public static final String TAG = "InitialComfortActivity";
    private EditText etZero;
    private EditText etFive;
    private EditText etTen;
    private Button btnConfirm;
    private ParseUser user;

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
                int tempzero = Integer.parseInt(etZero.getText().toString());
                int tempfive = Integer.parseInt(etFive.getText().toString());
                int tempten = Integer.parseInt(etTen.getText().toString());
                save(user, tempzero, tempfive, tempten);
            }
        });
    }

    private void save(ParseUser user, int tempzero, int tempfive, int tempten) {
        ComfortLevelEntry entryZero = new ComfortLevelEntry(user, tempzero, 0);
        ComfortLevelEntry entryFive = new ComfortLevelEntry(user, tempfive, 5);
        ComfortLevelEntry entryTen = new ComfortLevelEntry(user, tempten, 10);

        saveComfortLevel(entryZero);
        saveComfortLevel(entryFive);
        saveComfortLevel(entryTen);

        createLevels();

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

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}
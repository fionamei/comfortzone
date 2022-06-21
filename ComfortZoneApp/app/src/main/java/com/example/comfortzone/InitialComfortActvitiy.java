package com.example.comfortzone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.example.comfortzone.models.LevelsTracker;
import com.parse.SaveCallback;

public class InitialComfortActvitiy extends AppCompatActivity {

    public static final String TAG = "InitialComfortActivity";
    private EditText etZero;
    private EditText etFive;
    private EditText etTen;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_comfort_actvitiy);

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
                ParseUser currentUser = ParseUser.getCurrentUser();
                int zero = Integer.parseInt(etZero.getText().toString());
                int five = Integer.parseInt(etFive.getText().toString());
                int ten = Integer.parseInt(etTen.getText().toString());
                save(currentUser, zero, five, ten);
            }
        });
    }

    private void save(ParseUser currentUser, int zero, int five, int ten) {
        LevelsTracker tracker = new LevelsTracker();
        tracker.setZero(zero);
        tracker.setFive(five);
        tracker.setTen(ten);
        tracker.setUser(currentUser);
        tracker.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while saving " + e);
                } else {
                    goMainActivity();
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
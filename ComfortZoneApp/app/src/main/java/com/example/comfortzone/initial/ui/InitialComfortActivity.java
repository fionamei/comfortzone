package com.example.comfortzone.initial.ui;

import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comfortzone.R;
import com.example.comfortzone.initial.util.InitialDataUtil;
import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.ui.HostActivity;
import com.example.comfortzone.utils.ComfortCalcUtil;
import com.example.comfortzone.utils.ComfortLevelUtil;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    private RadioButton rbtnFahrenheit;

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
        rbtnFahrenheit = findViewById(R.id.rbtnFahrenheit);
    }

    private void confirmListener() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempZero = Integer.parseInt(etZero.getText().toString());
                int tempFive = Integer.parseInt(etFive.getText().toString());
                int tempTen = Integer.parseInt(etTen.getText().toString());
                if (tempZero < tempFive && tempFive < tempTen) {
                    if (!rbtnFahrenheit.isChecked()) {
                        tempZero = UserPreferenceUtil.convertCelsiusToFahrenheit(tempZero);
                        tempFive = UserPreferenceUtil.convertCelsiusToFahrenheit(tempFive);
                        tempTen = UserPreferenceUtil.convertCelsiusToFahrenheit(tempTen);
                    }
                    InitialDataUtil.saveInitialLevels(user, tempZero, tempFive, tempTen);
                    UserPreferenceUtil.updateIsFahrenheitLocally(InitialComfortActivity.this, rbtnFahrenheit.isChecked());
                    goHostActivity();
                } else {
                    Toast.makeText(InitialComfortActivity.this, "Your temperature estimates must be in ascending order", Toast.LENGTH_LONG).show();
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
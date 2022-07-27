package com.example.comfortzone.notification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.comfortzone.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationActivity extends AppCompatActivity {

    private TextView tvNotificationTime;
    private SwitchMaterial swNotification;
    private SharedPreferences sharedPref;
    public static final String KEY_IS_NOTIFICATION = "isNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sharedPref = getSharedPreferences(getString(R.string.key_shared_pref_activity), MODE_PRIVATE);

        initViews();
        populateViews();
        setUpListeners();
    }

    private void initViews() {
        tvNotificationTime = findViewById(R.id.tvNotificationTime);
        swNotification = findViewById(R.id.swNotification);
    }

    private void populateViews() {
        boolean isNotification = sharedPref.getBoolean(KEY_IS_NOTIFICATION, true);
        swNotification.setChecked(isNotification);
    }

    private void setUpListeners() {
        setNotificationListener();
        setTimeListener();
    }

    private void setNotificationListener() {
        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(KEY_IS_NOTIFICATION, isChecked);
                editor.apply();
            }
        });
    }

    private void setTimeListener() {

    }

}
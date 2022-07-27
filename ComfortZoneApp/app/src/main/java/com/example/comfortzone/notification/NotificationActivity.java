package com.example.comfortzone.notification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.comfortzone.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationActivity extends AppCompatActivity {

    private TextView tvNotificationTime;
    private SwitchMaterial swNotification;
    public static final String KEY_IS_NOTIFICATION = "isNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();
        populateViews();
        setUpListeners();
    }

    private void initViews() {
        tvNotificationTime = findViewById(R.id.tvNotificationTime);
        swNotification = findViewById(R.id.swNotification);
    }

    private void populateViews() {
        swNotification.setChecked(NotificationUtil.isNotificationEnabled(this));
    }

    private void setUpListeners() {
        setNotificationListener();
        setTimeListener();
    }

    private void setNotificationListener() {
        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationUtil.updateNotificationLocally(NotificationActivity.this, isChecked);
                if (isChecked) {
                    NotificationUtil.createNotification(NotificationActivity.this);
                } else {
                    NotificationUtil.cancelNotification(NotificationActivity.this);
                }
            }
        });
    }

    private void setTimeListener() {

    }

}
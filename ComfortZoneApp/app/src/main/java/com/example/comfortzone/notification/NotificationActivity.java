package com.example.comfortzone.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.example.comfortzone.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;

public class NotificationActivity extends AppCompatActivity {

    public static final String KEY_IS_NOTIFICATION = "isNotification";
    public static final String TAG = "notificationActivity";

    private MaterialButton btnNotificationTime;
    private SwitchMaterial swNotification;
    private Pair<Integer, Integer> savedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        savedTime = NotificationUtil.getNotificationTime(NotificationActivity.this);


        initViews();
        populateViews();
        setUpListeners();
    }

    private void initViews() {
        btnNotificationTime = findViewById(R.id.btnNotificationTime);
        swNotification = findViewById(R.id.swNotification);
    }

    private void populateViews() {
        swNotification.setChecked(NotificationUtil.isNotificationEnabled(this));
        btnNotificationTime.setText(NotificationUtil.formatPickedTime(savedTime.first, savedTime.second));
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
        btnNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setTitleText(getResources().getString(R.string.pick_notification))
                        .setHour(savedTime.first)
                        .setMinute(savedTime.second)
                        .build();
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                picker.show(supportFragmentManager, TAG);
                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pickedHour = picker.getHour();
                        int pickedMinute = picker.getMinute();
                        NotificationUtil.saveNotificationTime(NotificationActivity.this, pickedHour, pickedMinute);
                        StringBuilder timePicked = NotificationUtil.formatPickedTime(pickedHour, pickedMinute);
                        btnNotificationTime.setText(timePicked);
                    }
                });
            }
        });
    }
}
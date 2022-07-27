package com.example.comfortzone.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.comfortzone.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;

public class NotificationActivity extends AppCompatActivity {

    private TextView tvNotificationTime;
    private SwitchMaterial swNotification;
    public static final String KEY_IS_NOTIFICATION = "isNotification";
    public static final String TAG = "notificationActivity";

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
        tvNotificationTime.setText("10:00AM");
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
        tvNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                        .setTitleText(getResources().getString(R.string.pick_notification))
                        .setHour(10) // temp
                        .setMinute(0)
                        .build();
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                picker.show(supportFragmentManager, TAG);
                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pickedHour = picker.getHour();
                        int pickedMinute = picker.getMinute();
                        StringBuilder timePicked = NotificationUtil.formatPickedTime(pickedHour, pickedMinute);
                        tvNotificationTime.setText(timePicked);
                    }
                });
            }
        });
    }


}
package com.example.comfortzone.listener;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.comfortzone.callback.DegreeSwitchCallback;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.utils.UserPreferenceUtil;

public class DegreeSwitchListener {

    private TextView tvFahrenheit;
    private TextView tvCelsius;
    private Activity activity;

    public DegreeSwitchListener(TextView tvFahrenheit, TextView tvCelsius, Activity activity) {
        this.tvFahrenheit = tvFahrenheit;
        this.tvCelsius = tvCelsius;
        this.activity = activity;
    }

    public void setDegreeListeners(DegreeSwitchCallback degreeSwitchCallback) {
        tvCelsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((UserDetailsProvider) activity).getIsFahrenheit()){
                    switchIsFahrenheit(false);
                    degreeSwitchCallback.onDegreeSwitched();
                    UserPreferenceUtil.switchBoldedDegree(activity, tvCelsius, tvFahrenheit);
                }
            }
        });
        tvFahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((UserDetailsProvider) activity).getIsFahrenheit()){
                    switchIsFahrenheit(true);
                    degreeSwitchCallback.onDegreeSwitched();
                    UserPreferenceUtil.switchBoldedDegree(activity, tvCelsius, tvFahrenheit);
                }
            }
        });
    }

    private void switchIsFahrenheit(boolean bool) {
        ((UserDetailsProvider) activity).setIsFahrenheit(bool);
    }
}

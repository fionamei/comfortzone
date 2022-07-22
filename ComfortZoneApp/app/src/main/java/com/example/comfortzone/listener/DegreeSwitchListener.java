package com.example.comfortzone.listener;

import android.view.View;
import android.widget.TextView;

import com.example.comfortzone.callback.DegreeSwitchCallback;
import com.example.comfortzone.utils.UserPreferenceUtil;

public class DegreeSwitchListener {

    private TextView tvFahrenheit;
    private TextView tvCelsius;
    private Boolean[] isFahrenheit;

    public DegreeSwitchListener(TextView tvFahrenheit, TextView tvCelsius, Boolean[] isFahrenheit) {
        this.tvFahrenheit = tvFahrenheit;
        this.tvCelsius = tvCelsius;
        this.isFahrenheit = isFahrenheit;
    }

    public void degreeListeners(DegreeSwitchCallback degreeSwitchCallback) {
        tvCelsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFahrenheit[0]){
                    switchIsFahrenheit(false);
                    degreeSwitchCallback.onDegreeSwitched();
                    UserPreferenceUtil.switchBoldedDegree(isFahrenheit, tvCelsius, tvFahrenheit);
                }
            }
        });
        tvFahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFahrenheit[0]){
                    switchIsFahrenheit(true);
                    degreeSwitchCallback.onDegreeSwitched();
                    UserPreferenceUtil.switchBoldedDegree(isFahrenheit, tvCelsius, tvFahrenheit);
                }
            }
        });
    }

    private void switchIsFahrenheit(boolean bool) {
        isFahrenheit[0] = bool;
    }
}

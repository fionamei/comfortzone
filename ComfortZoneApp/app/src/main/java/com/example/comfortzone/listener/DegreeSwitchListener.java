package com.example.comfortzone.listener;

import android.view.View;
import android.widget.TextView;

import com.example.comfortzone.callback.DegreeSwitchCallback;

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
                    degreeSwitchCallback.onDegreeSwitched();
                    switchIsFahrenheit(false);
                }
            }
        });
        tvFahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFahrenheit[0]){
                    degreeSwitchCallback.onDegreeSwitched();
                    switchIsFahrenheit(true);
                }
            }
        });
    }

    private void switchIsFahrenheit(boolean bool) {
        isFahrenheit[0] = bool;
    }
}

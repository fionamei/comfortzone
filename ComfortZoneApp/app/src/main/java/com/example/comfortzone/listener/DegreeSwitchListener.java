package com.example.comfortzone.listener;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class DegreeSwitchListener {

    private TextView tvFahrenheit;
    private TextView tvCelsius;
    private Boolean[] isFahrenheit;

    public DegreeSwitchListener(TextView tvFahrenheit, TextView tvCelsius, Boolean[] isFahrenheit) {
        this.tvFahrenheit = tvFahrenheit;
        this.tvCelsius = tvCelsius;
        this.isFahrenheit = isFahrenheit;
    }

    public void degreeListeners() {
        tvCelsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFahrenheit[0]){
                    tvCelsius.setTypeface(Typeface.DEFAULT_BOLD);
                    tvFahrenheit.setTypeface(Typeface.DEFAULT);
                    switchIsFahrenheit(false);
                }
            }
        });
        tvFahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFahrenheit[0]){
                    tvCelsius.setTypeface(Typeface.DEFAULT);
                    tvFahrenheit.setTypeface(Typeface.DEFAULT_BOLD);
                    switchIsFahrenheit(true);
                }
            }
        });
    }

    private void switchIsFahrenheit(boolean bool) {
        isFahrenheit[0] = bool;
    }
}

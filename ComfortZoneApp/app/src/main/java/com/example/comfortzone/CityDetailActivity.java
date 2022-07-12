package com.example.comfortzone;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.comfortzone.models.WeatherData;

public class CityDetailActivity extends AppCompatActivity {

    public static final String ARG_CITY_ID = "cityId";
    public static final int IMAGE_RADIUS = 20;
    private WeatherData cityData;
    private TextView tvCityName;
    private ImageView ivCityIcon;
    private TextView tvCityDescription;

    private int cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);

        cityId = getIntent().getIntExtra(ARG_CITY_ID, 0);
        AllWeathersDatabase db = AllWeathersDatabase.getDbInstance(this.getApplicationContext());
        cityData = db.weatherDao().getWeatherById(cityId);

        initViews();
        populateViews();
    }

    private void initViews() {
        tvCityName = findViewById(R.id.tvCityName);
        ivCityIcon = findViewById(R.id.ivCityIcon);
        tvCityDescription = findViewById(R.id.tvCityDescription);
    }

    private void populateViews() {
        tvCityName.setText(String.format("%s, %s", cityData.getCity(), cityData.getState()));
        tvCityDescription.setText(cityData.getDescription());
        Glide.with(this).load(cityData.getImage()).transform(new RoundedCorners(IMAGE_RADIUS)).into(ivCityIcon);
    }
}
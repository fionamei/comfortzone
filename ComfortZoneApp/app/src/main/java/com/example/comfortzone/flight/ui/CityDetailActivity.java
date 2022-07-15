package com.example.comfortzone.flight.ui;

import static com.example.comfortzone.flight.ui.FlightFragment.LOC_IATA;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.comfortzone.R;
import com.example.comfortzone.data.local.AllWeathersDatabase;
import com.example.comfortzone.flight.callbacks.FlightBookingsCallback;
import com.example.comfortzone.flight.data.BookingClient;
import com.example.comfortzone.flight.models.Bookings.FlightBookings;
import com.example.comfortzone.models.WeatherData;

public class CityDetailActivity extends AppCompatActivity {

    public static final String TAG = "CityDetailActivity";
    public static final String ARG_CITY_ID = "cityId";
    public static final int IMAGE_RADIUS = 20;

    private WeatherData cityData;
    private TextView tvCityName;
    private ImageView ivCityIcon;
    private TextView tvCityDescription;
    private Button btnBookFlight;

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
        bookFlightListener();
    }

    private void initViews() {
        tvCityName = findViewById(R.id.tvCityName);
        ivCityIcon = findViewById(R.id.ivCityIcon);
        tvCityDescription = findViewById(R.id.tvCityDescription);
        btnBookFlight = findViewById(R.id.btnBookFlight);
    }

    private void populateViews() {
        tvCityName.setText(String.format("%s, %s", cityData.getCity(), cityData.getState()));
        tvCityDescription.setText(cityData.getDescription());
        Glide.with(this).load(cityData.getImage()).transform(new RoundedCorners(IMAGE_RADIUS)).into(ivCityIcon);
    }

    private void bookFlightListener() {
        btnBookFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingClient client = new BookingClient();
                client.getBookingLinks(LOC_IATA, cityData.getIata(), new FlightBookingsCallback() {
                    @Override
                    public void onFlightBookingList(FlightBookings flightBookings) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(flightBookings.getDeep_link())));
                    }
                });
            }
        });
    }
}
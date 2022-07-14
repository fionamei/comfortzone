package com.example.comfortzone.flight.ui;

import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_IATA;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comfortzone.R;
import com.example.comfortzone.flight.data.BookingClient;

public class BookFlightActivity extends AppCompatActivity {

    public static final String TAG = "BookFlightActivity";
    private String IATA;
    private BookingClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_flight);

        IATA = getIntent().getStringExtra(ARG_IATA);
        client = new BookingClient();

    }
}
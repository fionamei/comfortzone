package com.example.comfortzone.fragments;

import static com.example.comfortzone.models.ComfortLevelEntry.KEY_LEVEL_TRACKER;
import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;
import static com.example.comfortzone.utils.WeatherDbUtil.maybeUpdateCitiesList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.AllWeathersDatabase;
import com.example.comfortzone.FlightsAdapter;
import com.example.comfortzone.R;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.WeatherData;
import com.google.android.material.slider.RangeSlider;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FlightFragment extends Fragment {

    public static final String TAG = "FlightFragment";
    private FlightsAdapter flightsAdapter;
    private List<WeatherData> cityList;
    private RecyclerView rvCities;
    private AllWeathersDatabase db;
    private RangeSlider rsComfortFilter;

    public FlightFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cityList = new ArrayList<>();
        flightsAdapter = new FlightsAdapter(getContext(), cityList);
        db = AllWeathersDatabase.getDbInstance(getContext().getApplicationContext());

        initViews(view);
        maybeUpdateCitiesList(getContext());
        populateViews();
        listenerSetup();
    }



    private void initViews(@NonNull View view) {
        rvCities = view.findViewById(R.id.rvCities);
        rsComfortFilter = view.findViewById(R.id.rsComfortFilter);
    }

    private void populateViews() {
        rvCities.setAdapter(flightsAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
        flightsAdapter.addAll(db.weatherDao().getAll());
    }

    private void listenerSetup() {
        rsComfortFilter.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                int lowComfort = values.get(0).intValue();
                int highComfort = values.get(1).intValue();
                ParseUser currentUser = ParseUser.getCurrentUser();
                int lowRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS)).get(lowComfort).getLowRange();
                int highRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS)).get(highComfort).getHighRange();
                List<WeatherData> weathers = db.weatherDao().getRange(lowRange, highRange);
            }
        });
    }





}
package com.example.comfortzone.fragments;

import static com.example.comfortzone.utils.WeatherDbUtil.maybeUpdateCitiesList;

import android.os.Bundle;
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
import com.example.comfortzone.models.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class FlightFragment extends Fragment {

    public static final String TAG = "FlightFragment";
    private FlightsAdapter flightsAdapter;
    private List<WeatherData> cityList;
    private RecyclerView rvCities;
    private AllWeathersDatabase db;

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
        populateViews();
    }



    private void initViews(@NonNull View view) {
        rvCities = view.findViewById(R.id.rvCities);
    }

    private void populateViews() {
        rvCities.setAdapter(flightsAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
        flightsAdapter.addAll(db.weatherDao().getAll());
    }





}
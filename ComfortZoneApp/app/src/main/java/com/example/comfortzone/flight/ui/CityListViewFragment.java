package com.example.comfortzone.flight.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.R;
import com.example.comfortzone.flight.callbacks.UpdateCityListCallback;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.ui.HostActivity;

import java.util.ArrayList;
import java.util.List;

public class CityListViewFragment extends Fragment implements UpdateCityListCallback {

    public static final String TAG = "CityListViewFragment";

    private FlightsAdapter flightsAdapter;
    private RecyclerView rvCities;
    private List<WeatherData> cityList;

    public CityListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        cityList = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setObjects();
        populateViews();
    }

    private void initViews(@NonNull View view) {
        rvCities = view.findViewById(R.id.rvCities);
    }

    private void setObjects() {
        flightsAdapter = new FlightsAdapter(getContext(), cityList, ((HostActivity) getActivity()).getIataCode());
    }

    private void populateViews() {
        rvCities.setAdapter(flightsAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCityListUpdated(List<WeatherData> newCityList) {
        if (newCityList != null) {
            flightsAdapter.updateCities(newCityList);
        }
    }
}
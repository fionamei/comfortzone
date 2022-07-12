package com.example.comfortzone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.comfortzone.AllWeathersDatabase;
import com.example.comfortzone.FlightsAdapter;
import com.example.comfortzone.R;
import com.example.comfortzone.models.WeatherData;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CityListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityListViewFragment extends Fragment {

    public static final String ARG_CITY_LIST = "cityList";
    public static final String TAG = "CityListViewFragment";

    private FlightsAdapter flightsAdapter;
    private RecyclerView rvCities;
    private List<WeatherData> cityList;

    public CityListViewFragment() {
        // Required empty public constructor
    }

    public static CityListViewFragment newInstance(List<WeatherData> cityList) {
        CityListViewFragment fragment = new CityListViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CITY_LIST, Parcels.wrap(cityList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityList = Parcels.unwrap((getArguments().getParcelable(ARG_CITY_LIST)));

        }
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
        flightsAdapter = new FlightsAdapter(getContext(), cityList);
    }

    private void populateViews() {
        rvCities.setAdapter(flightsAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
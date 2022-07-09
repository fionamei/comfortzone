package com.example.comfortzone.fragments;

import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.StringUtil;

import com.example.comfortzone.AllWeathersDatabase;
import com.example.comfortzone.FlightsAdapter;
import com.example.comfortzone.R;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.WeatherDbUtil;
import com.google.android.material.slider.RangeSlider;
import com.parse.ParseUser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FlightFragment extends Fragment {

    public static final String TAG = "FlightFragment";
    private FlightsAdapter flightsAdapter;
    private List<WeatherData> cityList;
    private RecyclerView rvCities;
    private AllWeathersDatabase db;
    private RangeSlider rsComfortFilter;
    private Spinner spSort;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private EditText etSearchCity;

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

        initViews(view);
        setObjects();
        WeatherDbUtil.maybeUpdateCitiesList(getActivity());
        populateViews();
        listenerSetup();
    }

    private void initViews(@NonNull View view) {
        rvCities = view.findViewById(R.id.rvCities);
        rsComfortFilter = view.findViewById(R.id.rsComfortFilter);
        spSort = view.findViewById(R.id.spSort);
        etSearchCity = view.findViewById(R.id.etSearchCity);
    }

    private void setObjects() {
        cityList = new ArrayList<>();
        flightsAdapter = new FlightsAdapter(getContext(), cityList);
        db = AllWeathersDatabase.getDbInstance(getContext().getApplicationContext());
        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.filter_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void populateViews() {
        rvCities.setAdapter(flightsAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
        flightsAdapter.addAll(db.weatherDao().getAll());
        spSort.setAdapter(spinnerAdapter);
    }

    private void listenerSetup() {
        setFilterListener();
        setSortListener();
        setSearchCityListener();
    }

    private void setFilterListener() {
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
                flightsAdapter.filterClearAndAdd(weathers);
                sortBy(spSort.getSelectedItemPosition());
            }
        });
    }

    private void setSortListener() {
        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void sortBy(int position) {
        switch (position) {
            case 0:
                flightsAdapter.sortAlphabetical();
                break;
            case 1:
                flightsAdapter.sortIncTemp();
                break;
            case 2:
                flightsAdapter.sortDecTemp();
                break;
            case 3:
                flightsAdapter.sortDecDist();
                break;
            case 4:
                flightsAdapter.sortIncDist();
                break;
            case 5:
                flightsAdapter.sortDecRank();
                break;
            case 6:
                flightsAdapter.sortIncRank();
                break;
        }
    }

    private void setSearchCityListener() {
        etSearchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String city = Normalizer.normalize(s, Normalizer.Form.NFD);
                city = city.replaceAll("[^\\p{ASCII}]", "");
                flightsAdapter.searchCity(city.toLowerCase(Locale.ROOT), cityList);

            }
        });

    }




}
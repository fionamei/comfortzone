package com.example.comfortzone.flight.ui;


import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;

import android.graphics.Typeface;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.comfortzone.R;
import com.example.comfortzone.callback.DegreeSwitchCallback;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.data.local.AllWeathersDatabase;
import com.example.comfortzone.flight.utils.FilteringUtils;
import com.example.comfortzone.listener.DegreeSwitchListener;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.WeatherData;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.RangeSlider;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class FlightFragment extends Fragment {

    public static final String TAG = "FlightFragment";
    private final static int ALPHA = 0;
    private final static int INC_TEMP = 1;
    private final static int DEC_TEMP = 2;
    private final static int DIST_NEAR = 3;
    private final static int DIST_FAR = 4;
    private final static int INC_POP = 5;
    private final static int DEC_POP = 6;

    private List<WeatherData> cityList;
    private AllWeathersDatabase db;
    private RangeSlider rsComfortFilter;
    private Spinner spSort;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private EditText etSearchCity;
    private MaterialButtonToggleGroup tgCityDisplay;
    private FragmentManager fragmentManager;
    private CityMapViewFragment cityMapViewFragment;
    private CityListViewFragment cityListViewFragment;

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
        createFragments();
        listenerSetup();
        populateViews();
    }

    private void initViews(@NonNull View view) {
        rsComfortFilter = view.findViewById(R.id.rsComfortFilter);
        spSort = view.findViewById(R.id.spSort);
        etSearchCity = view.findViewById(R.id.etSearchCity);
        tgCityDisplay = view.findViewById(R.id.tgCityDisplay);
    }

    private void setObjects() {
        cityList = new ArrayList<>();
        db = AllWeathersDatabase.getDbInstance(getContext().getApplicationContext());
        spinnerAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.filter_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    private void createFragments() {
        cityMapViewFragment = new CityMapViewFragment();
        cityListViewFragment = new CityListViewFragment();
    }

    private void populateViews() {
        cityList.addAll(db.weatherDao().getAll());
        spSort.setAdapter(spinnerAdapter);
        tgCityDisplay.check(R.id.btnList);
    }

    private void listenerSetup() {
        setFilterListener();
        setSortListener();
        setSearchCityListener();
        displayToggleListener();
    }

    private void setFilterListener() {
        rsComfortFilter.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                Log.i(TAG, "city list before " + cityList);
                FilteringUtils.comfortLevelFilter(rsComfortFilter, cityList, db);
                Log.i(TAG, "city list after " + cityList);
                sortBy(spSort.getSelectedItemPosition());
                cityList = FilteringUtils.searchCity(etSearchCity.getText(), cityList);
                updateViewsList(cityList);
            }
        });
    }

    private void setSortListener() {
        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy(position);
                updateViewsList(cityList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void sortBy(int position) {
        switch (position) {
            case ALPHA:
                FilteringUtils.sortAlphabetical(cityList);
                break;
            case INC_TEMP:
                FilteringUtils.sortIncTemp(cityList);
                break;
            case DEC_TEMP:
                FilteringUtils.sortDecTemp(cityList);
                break;
            case DIST_NEAR:
                FilteringUtils.sortDecDist(cityList);
                break;
            case DIST_FAR:
                FilteringUtils.sortIncDist(cityList);
                break;
            case INC_POP:
                FilteringUtils.sortDecRank(cityList);
                break;
            case DEC_POP:
                FilteringUtils.sortIncRank(cityList);
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
                FilteringUtils.comfortLevelFilter(rsComfortFilter, cityList, db);
                if (!s.toString().isEmpty()) {
                    String city = Normalizer.normalize(s, Normalizer.Form.NFD);
                    city = city.replaceAll("[^\\p{ASCII}]", "");
                    cityList = FilteringUtils.searchCity(city, cityList);
                }
                sortBy(spSort.getSelectedItemPosition());
                updateViewsList(cityList);
            }
        });
    }

    private void displayToggleListener() {
        tgCityDisplay.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == R.id.btnList && isChecked) {
                    goToDisplay(cityListViewFragment);
                } else if (checkedId == R.id.btnMap && isChecked) {
                    goToDisplay(cityMapViewFragment);
                }
            }
        });
    }

    private void updateViewsList(List<WeatherData> cityList) {
        if (cityListViewFragment != null) {
            cityListViewFragment.onCityListUpdated(cityList);
        }
        if (cityMapViewFragment != null) {
            cityMapViewFragment.onCityListUpdated(cityList);
        }
    }

    private void goToDisplay(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.flViewsContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}
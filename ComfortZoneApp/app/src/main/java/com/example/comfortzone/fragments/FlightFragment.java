package com.example.comfortzone.fragments;

import static com.example.comfortzone.utils.ComfortCalcUtil.KEY_LEVEL_TRACKERS;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.comfortzone.AllWeathersDatabase;
import com.example.comfortzone.FlightsAdapter;
import com.example.comfortzone.R;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.FilteringUtils;
import com.example.comfortzone.utils.WeatherDbUtil;
import com.google.android.material.slider.RangeSlider;
import com.parse.ParseUser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

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

    private enum POSITIONS {
        ALPHA(0), INC_TEMP(1), DEC_TEMP(2), DIST_NEAR(3), DIST_FAR(4), INC_POP(5), DEC_POP(6);

        private int position;

        private POSITIONS(int position) {
            this.position = position;
        }

        static Map<Integer, POSITIONS> positionsMap = new HashMap<>();

        static {
            for (POSITIONS position_idx : POSITIONS.values()) {
                positionsMap.put(position_idx.position, position_idx);
            }
        }

        private static POSITIONS getPosition(int position) {
            return positionsMap.get(position);
        }
    }

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
        checkCitiesSaved();
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

    private void checkCitiesSaved() {
        Observable<Object> dataSetupObservable = WeatherDbUtil.maybeUpdateCitiesList(getActivity());
        Subscriber dataSetupSubscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateViews();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(Object o) {}
        };
        dataSetupObservable.subscribe(dataSetupSubscriber);
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
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                int lowComfort = values.get(0).intValue();
                int highComfort = values.get(1).intValue();
                ParseUser currentUser = ParseUser.getCurrentUser();
                int lowRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS)).get(lowComfort).getLowRange();
                int highRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS)).get(highComfort).getHighRange();
                List<WeatherData> weathers = db.weatherDao().getRange(lowRange, highRange);
                flightsAdapter.updateCities(weathers);
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void sortBy(int position) {
        POSITIONS pos = POSITIONS.getPosition(position);
        switch (pos) {
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
        flightsAdapter.notifyDataSetChanged();
    }

    private void setSearchCityListener() {
        etSearchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String city = Normalizer.normalize(s, Normalizer.Form.NFD);
                city = city.replaceAll("[^\\p{ASCII}]", "");
                flightsAdapter.searchCity(city.toLowerCase(Locale.ROOT), cityList);
            }
        });

    }




}
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
import androidx.fragment.app.FragmentManager;

import com.example.comfortzone.AllWeathersDatabase;
import com.example.comfortzone.R;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.FilteringUtils;
import com.example.comfortzone.utils.WeatherDbUtil;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.RangeSlider;
import com.parse.ParseUser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;

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
    private MapFragment mapFragment;
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
        checkCitiesSaved();
        listenerSetup();
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
        mapFragment = new MapFragment();
        cityListViewFragment = new CityListViewFragment();
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
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
            }
        };
        dataSetupObservable.subscribe(dataSetupSubscriber);
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
                etSearchCity.setText("");
                List<Float> values = slider.getValues();
                int lowComfort = values.get(0).intValue();
                int highComfort = values.get(1).intValue();
                ParseUser currentUser = ParseUser.getCurrentUser();
                int lowRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS))
                        .get(lowComfort).getLowRange();
                int highRange = ((ArrayList<LevelsTracker>) currentUser.get(KEY_LEVEL_TRACKERS))
                        .get(highComfort).getHighRange();
                cityList = db.weatherDao().getRange(lowRange, highRange);
                sortBy(spSort.getSelectedItemPosition());
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
                String city = Normalizer.normalize(s, Normalizer.Form.NFD);
                city = city.replaceAll("[^\\p{ASCII}]", "");
                List<WeatherData> searchedCity = FilteringUtils.searchCity(city.toLowerCase(Locale.ROOT), cityList);
                updateViewsList(searchedCity);
            }
        });
    }

    private void displayToggleListener() {
        tgCityDisplay.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == R.id.btnList && isChecked) {
                    goToListView();
                } else if (checkedId == R.id.btnMap && isChecked) {
                    goToMapView();
                }
            }
        });
    }

    private void updateViewsList(List<WeatherData> cityList) {
        if (cityListViewFragment != null) {
            cityListViewFragment.onCityListUpdated(cityList);
        }
        if (mapFragment != null) {
            mapFragment.onCityListUpdated(cityList);
        }
    }

    private void goToListView() {
        fragmentManager.beginTransaction()
                .replace(R.id.flViewsContainer, cityListViewFragment)
                .addToBackStack(null)
                .commit();
    }

    private void goToMapView() {
        fragmentManager.beginTransaction()
                .replace(R.id.flViewsContainer, mapFragment)
                .addToBackStack(null)
                .commit();
    }

}
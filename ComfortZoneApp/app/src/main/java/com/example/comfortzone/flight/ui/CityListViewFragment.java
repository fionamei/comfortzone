package com.example.comfortzone.flight.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.R;
import com.example.comfortzone.callback.DegreeSwitchCallback;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.flight.callbacks.UpdateCityListCallback;
import com.example.comfortzone.listener.DegreeSwitchListener;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.UserPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class CityListViewFragment extends Fragment implements UpdateCityListCallback {

    public static final String TAG = "CityListViewFragment";

    private FlightsAdapter flightsAdapter;
    private RecyclerView rvCities;
    private List<WeatherData> cityList;
    private TextView tvFahrenheit;
    private TextView tvCelsius;
    private Boolean isFahrenheit;

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
        setDegreesListener();
    }

    private void initViews(@NonNull View view) {
        rvCities = view.findViewById(R.id.rvCities);
        tvFahrenheit = view.findViewById(R.id.tvFahrenheit);
        tvCelsius = view.findViewById(R.id.tvCelsius);
    }

    private void setObjects() {
        flightsAdapter = new FlightsAdapter(getActivity(), cityList, ((UserDetailsProvider) getActivity()).getIataCode());
        isFahrenheit = ((UserDetailsProvider) getActivity()).getIsFahrenheit();
    }

    private void populateViews() {
        rvCities.setAdapter(flightsAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
        if (isFahrenheit) {
            tvFahrenheit.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            tvCelsius.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    public void onCityListUpdated(List<WeatherData> newCityList) {
        if (newCityList != null && flightsAdapter != null) {
            flightsAdapter.updateCities(newCityList);
        }
    }

    private void setDegreesListener() {
        DegreeSwitchListener degreeSwitchListener = new DegreeSwitchListener(tvFahrenheit, tvCelsius, getActivity());
        degreeSwitchListener.setDegreeListeners(new DegreeSwitchCallback() {
            @Override
            public void onDegreeSwitched() {
                UserPreferenceUtil.updateIsFahrenheitLocally(getActivity(), ((UserDetailsProvider) getActivity()).getIsFahrenheit());
                flightsAdapter.notifyDataSetChanged();
            }
        });
    }
}
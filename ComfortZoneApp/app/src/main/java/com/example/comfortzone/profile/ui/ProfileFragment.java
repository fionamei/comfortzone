package com.example.comfortzone.profile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.R;
import com.example.comfortzone.callback.DegreeSwitchCallback;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.data.local.AllWeathersDatabase;
import com.example.comfortzone.listener.DegreeSwitchListener;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.profile.callback.SwipeToDeleteCallback;
import com.example.comfortzone.utils.ComfortCalcUtil;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileFragment extends Fragment {

    private TextView tvPerfectTemp;
    private ParseUser currentUser;
    private RecyclerView rvSavedCities;
    private SavedCitiesAdapter adapter;
    private TextView tvFahrenheit;
    private TextView tvCelsius;
    private Boolean isFahrenheit;
    private int perfectTemp;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) {
            return;
        }

        setDataObjects();
        initViews(view);
        populateViews();
        setUpRecyclerView();
        setSavedCitiesAdapter();
        setDegreesListener();
    }

    private void setDataObjects() {
        currentUser = ParseUser.getCurrentUser();
        List<WeatherData> savedCities = new ArrayList<>();
        adapter = new SavedCitiesAdapter(getActivity(), savedCities, ((UserDetailsProvider) getActivity()).getIataCode());
        isFahrenheit = ((UserDetailsProvider) getActivity()).getIsFahrenheit();
        perfectTemp = (int) currentUser.get(ComfortCalcUtil.KEY_PERFECT_COMFORT);
    }

    private void initViews(@NonNull View view) {
        tvPerfectTemp = view.findViewById(R.id.tvPerfectTemp);
        rvSavedCities = view.findViewById(R.id.rvSavedCities);
        tvFahrenheit = view.findViewById(R.id.tvFahrenheit);
        tvCelsius = view.findViewById(R.id.tvCelsius);
    }

    private void populateViews() {
        UserPreferenceUtil.degreeConversion(getActivity(), tvPerfectTemp, perfectTemp);
        UserPreferenceUtil.switchBoldedDegree(getActivity(), tvCelsius, tvFahrenheit);
    }

    private void setUpRecyclerView() {
        rvSavedCities.setAdapter(adapter);
        rvSavedCities.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext()));
        itemTouchHelper.attachToRecyclerView(rvSavedCities);
    }

    private void setSavedCitiesAdapter() {
        HashSet<Integer> savedCityIds = ((UserDetailsProvider) getActivity()).getSavedCities();
        if (savedCityIds != null || !savedCityIds.isEmpty()) {
            AllWeathersDatabase db = AllWeathersDatabase.getDbInstance(getContext());
            adapter.addAll(savedCityIds
                    .stream()
                    .map(cityId -> db.weatherDao().getWeatherById(cityId))
                    .collect(Collectors.toList()));
        }
    }

    private void setDegreesListener() {
        DegreeSwitchListener degreeSwitchListener = new DegreeSwitchListener(tvFahrenheit, tvCelsius, getActivity());
        degreeSwitchListener.setDegreeListeners(new DegreeSwitchCallback() {
            @Override
            public void onDegreeSwitched() {
                UserPreferenceUtil.degreeConversion(getActivity(), tvPerfectTemp, perfectTemp);
                UserPreferenceUtil.updateIsFahrenheitLocally(getActivity(), ((UserDetailsProvider) getActivity()).getIsFahrenheit());
            }
        });
    }
}
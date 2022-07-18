package com.example.comfortzone.profile;

import android.os.Bundle;
import android.util.Log;
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
import com.example.comfortzone.data.local.AllWeathersDatabase;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.ComfortCalcUtil;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileFragment extends Fragment {

    public static final String KEY_SAVED_CITIES = "savedCities";

    private TextView tvPerfectTemp;
    private ParseUser currentUser;
    private RecyclerView rvSavedCities;
    private SavedCitiesAdapter adapter;
    private List<WeatherData> savedCities;

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

        getDataObjects();
        initViews(view);
        populateViews();
        getSavedCities();
    }

    private void getDataObjects() {
        currentUser = ParseUser.getCurrentUser();
        savedCities = new ArrayList<>();
        adapter = new SavedCitiesAdapter(getContext(), savedCities);
    }

    private void initViews(@NonNull View view) {
        tvPerfectTemp = view.findViewById(R.id.tvPerfectTemp);
        rvSavedCities = view.findViewById(R.id.rvSavedCities);
    }

    private void populateViews() {
        rvSavedCities.setAdapter(adapter);
        rvSavedCities.setLayoutManager(new LinearLayoutManager(getContext()));
        tvPerfectTemp.setText(String.valueOf(currentUser.get(ComfortCalcUtil.KEY_PERFECT_COMFORT)));
    }

    public List<WeatherData> getSavedCities() {
        ArrayList<Integer> savedCityIds = (ArrayList<Integer>) currentUser.get(KEY_SAVED_CITIES);
        AllWeathersDatabase db = AllWeathersDatabase.getDbInstance(getContext());
        adapter.addAll(savedCityIds.stream().map(cityId -> db.weatherDao().getWeatherById(cityId)).collect(Collectors.toList()));
        return savedCities;
    }
}
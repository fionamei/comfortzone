package com.example.comfortzone.Fragments;

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
import com.example.comfortzone.CityListCallback;
import com.example.comfortzone.FlightsAdapter;
import com.example.comfortzone.GroupUrlCallback;
import com.example.comfortzone.R;
import com.example.comfortzone.WeatherClient;
import com.example.comfortzone.getWeatherCallback;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.models.WeatherGroupData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FlightFragment extends Fragment {

    public static final String TAG = "FlightFragment";
    private WeatherClient client;
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

        client = new WeatherClient();
        cityList = new ArrayList<>();
        flightsAdapter = new FlightsAdapter(getContext(), cityList);
        db = AllWeathersDatabase.getDbInstance(getContext().getApplicationContext());

        initViews(view);
//        db.weatherDao().deleteEntireTable();
        populateViews();
        maybeUpdateCitiesList();
    }



    private void initViews(@NonNull View view) {
        rvCities = view.findViewById(R.id.rvCities);
    }

    private void populateViews() {
        rvCities.setAdapter(flightsAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
        flightsAdapter.addAll(db.weatherDao().getAll());
    }

    public void maybeUpdateCitiesList() {
        if (db.weatherDao().getAll().isEmpty()) {
            client.getCityData(new CityListCallback() {
                @Override
                public void cityList(WeatherData[] weatherData) {
                    db.weatherDao().insertAll(weatherData);
                }
            });
        }
        Long timeNow = System.currentTimeMillis();
        Long hourAgo = timeNow - TimeUnit.HOURS.toMillis(1L);
        List<WeatherData> timesBeforeHour = db.weatherDao().getUploadTimes(hourAgo);
        if (!timesBeforeHour.isEmpty()) {
            updateCities();
        }
    }

    public void updateCities() {
        client.getGroupWeatherUrl(getContext(), new GroupUrlCallback() {
            @Override
            public void weatherUrlGroupIds(List<String> groupUrls) {
                for (String apiUrl : groupUrls) {
                    client.getGroupWeatherData(apiUrl, new getWeatherCallback() {
                        @Override
                        public void weatherData(String data) {
                            Gson gson = new Gson();
                            WeatherGroupData weathers = gson.fromJson(data, WeatherGroupData.class);
                            saveCities(weathers);
                        }
                    });
                }
            }
        });
    }

    public void saveCities(WeatherGroupData weathers) {
        WeatherData[] weatherData = weathers.getWeathers();
        AllWeathersDatabase db = AllWeathersDatabase.getDbInstance(getContext().getApplicationContext());
        for (WeatherData weather : weatherData) {
            int id = weather.getId();
            WeatherData weatherFromDb = db.weatherDao().getWeatherById(id);
            weatherFromDb.setTempData(weather.getTempData());
            weatherFromDb.setTimeUploaded(weather.getTimeUploaded());
            db.weatherDao().updateWeatherData(weatherFromDb);
        }
    }

}
package com.example.comfortzone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comfortzone.GetLocationCallback;
import com.example.comfortzone.R;
import com.example.comfortzone.Utils.LocationUtil;
import com.example.comfortzone.WeatherClient;
import com.example.comfortzone.getWeatherCallback;
import com.example.comfortzone.models.WeatherData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InputFragment extends Fragment {

    private TextView tvDate;
    private TextView tvCity;
    private TextView tvTime;
    private TextView tvCurrentTemp;
    private WeatherClient client;
    private WeatherData weatherData;

    public InputFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = new WeatherClient();
        initViews(view);
        getWeatherClass();

    }

    private void initViews(View view) {
        tvDate = view.findViewById(R.id.tvDate);
        tvCity = view.findViewById(R.id.tvCity);
        tvTime = view.findViewById(R.id.tvTime);
        tvCurrentTemp = view.findViewById(R.id.tvCurrentTemp);
    }

    private void populateViews() {
        tvCity.setText(weatherData.getCity());
        tvCurrentTemp.setText(String.valueOf(weatherData.getTempData().getTemp()));
        tvDate.setText(weatherData.getDate());
        tvTime.setText(weatherData.getTime());
    }

    private void getWeatherClass() {
        LocationUtil.getLastLocation(getActivity(), new GetLocationCallback() {
            @Override
            public void location(String lat, String lon) {
                client.getWeatherData(lat, lon, new getWeatherCallback() {
                    @Override
                    public void weatherData(String data) {
                        Gson gson = new GsonBuilder().create();
                        weatherData = gson.fromJson(data, WeatherData.class);
                        weatherData.setDate();
                        weatherData.setTime();
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                populateViews();
                            }
                        });
                    }
                });
            }
        });
    }
}
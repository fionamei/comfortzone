package com.example.comfortzone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comfortzone.GroupUrlCallback;
import com.example.comfortzone.R;
import com.example.comfortzone.WeatherClient;
import com.example.comfortzone.getWeatherCallback;
import com.google.gson.Gson;

import java.util.List;

public class FlightFragment extends Fragment {

    public static final String TAG = "FlightFragment";
    private WeatherClient client;

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
    }

    public void getCities() {
        client.getGroupWeatherUrl(new GroupUrlCallback() {
            @Override
            public void weatherUrlGroupIds(List<String> groupUrls) {
                for (String apiUrl : groupUrls) {
                    client.getGroupWeatherData(apiUrl, new getWeatherCallback() {
                        @Override
                        public void weatherData(String data) {
                            
                        }
                    });
                }
            }
        });
    }
}
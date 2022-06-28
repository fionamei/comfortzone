package com.example.comfortzone.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.GetLocationCallback;
import com.example.comfortzone.InputsAdapter;
import com.example.comfortzone.R;
import com.example.comfortzone.Utils.LocationUtil;
import com.example.comfortzone.Utils.ParseUtil;
import com.example.comfortzone.WeatherClient;
import com.example.comfortzone.getWeatherCallback;
import com.example.comfortzone.models.TodayEntry;
import com.example.comfortzone.models.WeatherData;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class InputFragment extends Fragment {

    private TextView tvDate;
    private TextView tvCity;
    private TextView tvTime;
    private TextView tvCurrentTemp;
    private WeatherClient client;
    private WeatherData weatherData;
    private Button btnSubmit;
    private Slider slComfortLevel;
    private ParseUser currentUser;
    private int temp;
    private RecyclerView rvInputs;
    private InputsAdapter adapter;
    private List<TodayEntry> entries;

    public static final String TAG = "InputFragment";
    public static final int DEFAULT_VALUE = 5;
    public static final String KEY_TODAY_ENTRIES = "todayEntries";

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

        initViews(view);
        getWeatherClass();
        queryInputs();
        listenerSetup();
    }

    private void initViews(View view) {
        client = new WeatherClient();
        currentUser = ParseUser.getCurrentUser();
        entries = new ArrayList<>();
        adapter = new InputsAdapter(getContext(), entries);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        tvDate = view.findViewById(R.id.tvDate);
        tvCity = view.findViewById(R.id.tvCity);
        tvTime = view.findViewById(R.id.tvTime);
        tvCurrentTemp = view.findViewById(R.id.tvCurrentTemp);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        slComfortLevel = view.findViewById(R.id.slComfortLevel);
        rvInputs = view.findViewById(R.id.rvInputs);
        rvInputs.setAdapter(adapter);
        rvInputs.setLayoutManager(linearLayoutManager);
    }

    private void populateViews() {
        tvCity.setText(weatherData.getCity());
        tvCurrentTemp.setText(String.valueOf(temp));
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
                        temp = (int) weatherData.getTempData().getTemp();
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

    private void listenerSetup() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int comfortLevel = (int) slComfortLevel.getValue();
                slComfortLevel.setValue(DEFAULT_VALUE);
                ParseUtil.updateEntriesList(currentUser, temp, comfortLevel);
            }
        });
    }

    private void queryInputs() {
        ParseQuery<TodayEntry> query = ParseQuery.getQuery(TodayEntry.class);
        query.include(TodayEntry.KEY_USER);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<TodayEntry>() {
            @Override
            public void done(List<TodayEntry> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error adding inputs" + e);
                }
                entries.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }




}
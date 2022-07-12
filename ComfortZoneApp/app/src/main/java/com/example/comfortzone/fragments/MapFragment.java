package com.example.comfortzone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comfortzone.R;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.models.WeatherData.Coordinates;
import com.example.comfortzone.utils.WeatherDbUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap googleMapFragment;
    MapView mvMap;
    public static final int MAX_CITIES = 20;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mvMap = view.findViewById(R.id.mvMap);
        if (mvMap != null) {
            mvMap.onCreate(null);
            mvMap.onResume();
            mvMap.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        googleMapFragment = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addMapMarkers(googleMap, WeatherDbUtil.getAll().subList(0, MAX_CITIES));
    }

    public void addMapMarkers(GoogleMap googleMap, List<WeatherData> weatherData) {
        for (WeatherData weather : weatherData) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(weather.getCoord().getLat(), weather.getCoord().getLon()))
                    .title(weather.getCity()));
        }
    }
}
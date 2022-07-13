package com.example.comfortzone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.comfortzone.R;
import com.example.comfortzone.UpdateCityListCallback;
import com.example.comfortzone.models.WeatherData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, UpdateCityListCallback {

    public static final int MAX_CITIES = 20;
    private final LatLng MAP_CENTER = new LatLng(39.8283, -98.5795);

    private MapView mvMap;
    private GoogleMap mGoogleMap;
    private List<WeatherData> cityList;

    public MapFragment() {
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
        mGoogleMap = googleMap;
        MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addMapMarkers(googleMap, reducedCityList());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(MAP_CENTER));
    }

    private List<WeatherData> reducedCityList() {
        if (cityList.size() > MAX_CITIES) {
            return cityList.subList(0, MAX_CITIES);
        } else {
            return cityList;
        }
    }

    public void addMapMarkers(GoogleMap googleMap, List<WeatherData> weatherData) {
        for (WeatherData weather : weatherData) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(weather.getCoord().getLat(), weather.getCoord().getLon()))
                    .title(weather.getCity()));
        }
    }

    @Override
    public void onCityListUpdated(List<WeatherData> newCityList) {
        if (mGoogleMap!= null) {
            mGoogleMap.clear();
            cityList = newCityList;
            addMapMarkers(mGoogleMap, reducedCityList());
        }
    }
}
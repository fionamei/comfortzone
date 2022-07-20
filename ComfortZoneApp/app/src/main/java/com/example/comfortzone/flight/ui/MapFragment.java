package com.example.comfortzone.flight.ui;

import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_CITY_ID;
import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_IATA;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.comfortzone.R;
import com.example.comfortzone.flight.callbacks.UpdateCityListCallback;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.ui.HostActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, UpdateCityListCallback {

    public static final String TAG= "MapFragment";
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
        initViews(view);
        createMap();
    }

    private void initViews(@NonNull View view) {
        mvMap = view.findViewById(R.id.mvMap);
    }

    private void createMap() {
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
        addMapMarkers(googleMap, getReducedCityList());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(MAP_CENTER));
        markerClickListener();
    }

    private List<WeatherData> getReducedCityList() {
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
                    .title(weather.getCity() + ", " + weather.getState()))
                    .setTag(weather.getId());
        }
    }

    @Override
    public void onCityListUpdated(List<WeatherData> newCityList) {
        cityList = newCityList;
        if (mGoogleMap!= null) {
            mGoogleMap.clear();
            addMapMarkers(mGoogleMap, getReducedCityList());
        }
    }

    private void markerClickListener() {
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                int cityId = (int) marker.getTag();
                Intent intent = new Intent(getContext(), CityDetailActivity.class);
                intent.putExtra(ARG_CITY_ID, cityId);
                intent.putExtra(ARG_IATA, ((HostActivity) getActivity()).getIataCode());
                getActivity().startActivity(intent);
            }
        });
    }
}
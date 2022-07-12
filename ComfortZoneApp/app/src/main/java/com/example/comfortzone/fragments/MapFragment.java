package com.example.comfortzone.fragments;

import static com.example.comfortzone.fragments.CityListViewFragment.ARG_CITY_LIST;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.comfortzone.R;
import com.example.comfortzone.models.WeatherData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mvMap;
    public static final int MAX_CITIES = 20;
    private List<WeatherData> cityList;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(List<WeatherData> cityList) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CITY_LIST, Parcels.wrap(cityList));
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityList = Parcels.unwrap((getArguments().getParcelable(ARG_CITY_LIST)));
        }
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
        MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addMapMarkers(googleMap, cityList.subList(0, MAX_CITIES));
    }

    public void addMapMarkers(GoogleMap googleMap, List<WeatherData> weatherData) {
        for (WeatherData weather : weatherData) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(weather.getCoord().getLat(), weather.getCoord().getLon()))
                    .title(weather.getCity()));
        }
    }
}
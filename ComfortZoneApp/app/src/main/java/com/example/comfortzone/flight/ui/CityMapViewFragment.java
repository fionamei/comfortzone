package com.example.comfortzone.flight.ui;

import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_CITY_ID;
import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_IATA;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.comfortzone.R;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.flight.callbacks.UpdateCityListCallback;
import com.example.comfortzone.models.WeatherData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class CityMapViewFragment extends Fragment implements OnMapReadyCallback, UpdateCityListCallback {

    public static final String TAG = "CityMapViewFragment";
    /** this location represents the center of the united states **/
    private static final LatLng MAP_CENTER = new LatLng(39.8283, -98.5795);
    private static final int INITIAL_POSITION = 2;

    private MapView mvMap;
    private GoogleMap mGoogleMap;
    private List<WeatherData> cityList;
    private Spinner spPinNum;
    private int maxCities = 20;

    public CityMapViewFragment() {
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
        setupSpinnerAdapter();
        setSpinnerListener();
        createMap();
    }

    private void initViews(@NonNull View view) {
        mvMap = view.findViewById(R.id.mvMap);
        spPinNum = view.findViewById(R.id.spPinNum);
    }

    public void setupSpinnerAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.num_of_pins_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPinNum.setAdapter(adapter);
        spPinNum.setSelection(INITIAL_POSITION);
    }

    public void setSpinnerListener() {
        spPinNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("All")) {
                    maxCities = -1;
                } else {
                    maxCities = Integer.parseInt((String) parent.getItemAtPosition(position));
                }
                refreshMap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        if (maxCities > 0 && cityList.size() > maxCities) {
            return cityList.subList(0, maxCities);
        } else {
            return cityList;
        }
    }

    public void addMapMarkers(GoogleMap googleMap, List<WeatherData> weatherData) {
        for (WeatherData weather : weatherData) {
            StringBuilder cityName = new StringBuilder();
            if (weather.getState().isEmpty()) {
                cityName.append(weather.getCity());
            } else {
                cityName.append(weather.getCity()).append(", ").append(weather.getState());
            }
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(weather.getCoord().getLat(), weather.getCoord().getLon()))
                    .title(cityName.toString()))
                    .setTag(weather.getId());
        }
    }

    @Override
    public void onCityListUpdated(List<WeatherData> newCityList) {
        cityList = newCityList;
        if (mGoogleMap!= null) {
            refreshMap();
        }
    }

    private void refreshMap() {
        mGoogleMap.clear();
        addMapMarkers(mGoogleMap, getReducedCityList());
    }

    private void markerClickListener() {
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                int cityId = (int) marker.getTag();
                Intent intent = new Intent(getContext(), CityDetailActivity.class);
                intent.putExtra(ARG_CITY_ID, cityId);
                intent.putExtra(ARG_IATA, ((UserDetailsProvider) getActivity()).getIataCode());
                getActivity().startActivity(intent);
            }
        });
    }
}
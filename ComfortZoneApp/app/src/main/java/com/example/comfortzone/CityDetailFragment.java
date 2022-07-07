package com.example.comfortzone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.comfortzone.models.WeatherData;

public class CityDetailFragment extends Fragment {

    private static final String ARG_CITY_ID = "cityId";
    public static final int IMAGE_RADIUS = 20;
    private WeatherData cityData;
    private TextView tvCityName;
    private ImageView ivCityIcon;
    private TextView tvCityDescription;

    private int cityId;

    public CityDetailFragment() {
        // Required empty public constructor
    }

    public static CityDetailFragment newInstance(int cityId) {
        CityDetailFragment fragment = new CityDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CITY_ID, cityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityId = getArguments().getInt(ARG_CITY_ID);
            AllWeathersDatabase db = AllWeathersDatabase.getDbInstance(getContext().getApplicationContext());
            cityData = db.weatherDao().getWeatherById(cityId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populateViews();
    }

    private void initViews(View view) {
        tvCityName = view.findViewById(R.id.tvCityName);
        ivCityIcon = view.findViewById(R.id.ivCityIcon);
        tvCityDescription = view.findViewById(R.id.tvCityDescription);
    }

    private void populateViews() {
        tvCityName.setText(String.format("%s, %s", cityData.getCity(), cityData.getState()));
        tvCityDescription.setText(cityData.getDescription());
        Glide.with(getContext()).load(cityData.getImage()).apply(new RequestOptions().dontTransform()).into(ivCityIcon);
        ivCityIcon.setTransitionName(getContext().getResources().getString(R.string.cityIcon));
    }
}
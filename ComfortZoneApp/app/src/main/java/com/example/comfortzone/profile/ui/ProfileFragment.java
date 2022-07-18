package com.example.comfortzone.profile.ui;

import static com.example.comfortzone.utils.UserPreferenceUtil.KEY_SAVED_CITIES;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.R;
import com.example.comfortzone.data.local.AllWeathersDatabase;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.profile.callback.SwipeToDeleteCallback;
import com.example.comfortzone.utils.ComfortCalcUtil;
import com.example.comfortzone.utils.WeatherDbUtil;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import rx.Observable;
import rx.Subscriber;

public class ProfileFragment extends Fragment {

    private TextView tvPerfectTemp;
    private ParseUser currentUser;
    private RecyclerView rvSavedCities;
    private SavedCitiesAdapter adapter;
    private List<WeatherData> savedCities;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDataObjects();
        initViews(view);
        populateViews();
        setUpRecyclerView();
        getSavedCities();
    }

    private void getDataObjects() {
        currentUser = ParseUser.getCurrentUser();
        savedCities = new ArrayList<>();
        adapter = new SavedCitiesAdapter(getContext(), savedCities);
    }

    private void initViews(@NonNull View view) {
        tvPerfectTemp = view.findViewById(R.id.tvPerfectTemp);
        rvSavedCities = view.findViewById(R.id.rvSavedCities);
    }

    private void populateViews() {
        tvPerfectTemp.setText(String.valueOf(currentUser.get(ComfortCalcUtil.KEY_PERFECT_COMFORT)));
    }

    private void setUpRecyclerView() {
        rvSavedCities.setAdapter(adapter);
        rvSavedCities.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(rvSavedCities);
    }

    private void getSavedCities() {
        Observable<Object> dataSetupObservable = WeatherDbUtil.maybeUpdateCitiesList(getActivity());
        Subscriber dataSetupSubscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Integer> savedCityIds = (ArrayList<Integer>) currentUser.get(KEY_SAVED_CITIES);
                        AllWeathersDatabase db = AllWeathersDatabase.getDbInstance(getContext());
                        adapter.addAll(savedCityIds.stream().map(cityId -> db.weatherDao().getWeatherById(cityId)).collect(Collectors.toList()));                    }
                });
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
            }
        };
        dataSetupObservable.subscribe(dataSetupSubscriber);
    }
}
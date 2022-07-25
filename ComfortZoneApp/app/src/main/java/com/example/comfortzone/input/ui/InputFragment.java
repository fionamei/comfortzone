package com.example.comfortzone.input.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.comfortzone.R;
import com.example.comfortzone.callback.DegreeSwitchCallback;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.listener.DegreeSwitchListener;
import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.LevelsTracker;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.ComfortLevelUtil;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.google.android.material.slider.Slider;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class InputFragment extends Fragment {

    public static final String TAG = "InputFragment";
    public static final int DEFAULT_VALUE = 5;
    public static final int INSERT_INDEX = 0;

    private TextView tvDate;
    private TextView tvCity;
    private TextView tvTime;
    private TextView tvCurrentTemp;
    private WeatherData weatherData;
    private Button btnSubmit;
    private Slider slComfortLevel;
    private ParseUser currentUser;
    private SwipeableRecyclerView rvInputs;
    private InputsAdapter adapter;
    private List<ComfortLevelEntry> entries;
    private TextView tvFahrenheit;
    private TextView tvCelsius;
    private ImageButton ibRefresh;

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
        currentUser = ParseUser.getCurrentUser();
        entries = new ArrayList<>();
        adapter = new InputsAdapter(getActivity(), entries);

        initViews(view);
        getWeatherData();
        setRefreshListener();
        queryInputs();
    }

    private void initViews(View view) {
        tvDate = view.findViewById(R.id.tvDate);
        tvCity = view.findViewById(R.id.tvCityName);
        tvTime = view.findViewById(R.id.tvTime);
        tvCurrentTemp = view.findViewById(R.id.tvCurrentTemp);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        slComfortLevel = view.findViewById(R.id.slComfortLevel);
        rvInputs = view.findViewById(R.id.rvInputs);
        rvInputs.setAdapter(adapter);
        rvInputs.setLayoutManager(new LinearLayoutManager(getContext()));
        tvFahrenheit = view.findViewById(R.id.tvFahrenheit);
        tvCelsius = view.findViewById(R.id.tvCelsius);
        ibRefresh = view.findViewById(R.id.ibRefresh);
    }

    private void populateViews() {
        tvCity.setText(weatherData.getCity());
        tvDate.setText(weatherData.getDate());
        tvTime.setText(weatherData.getTime());
        UserPreferenceUtil.degreeConversion(getActivity(), tvCurrentTemp, (int) weatherData.getTempData().getTemp());
        UserPreferenceUtil.switchBoldedDegree(getActivity(), tvCelsius, tvFahrenheit);
    }

    private void getWeatherData() {
        weatherData = ((UserDetailsProvider) getActivity()).getCurrentWeatherData();
        if (weatherData != null && weatherData.getTempData() != null) {
            populateViews();
            listenerSetup();
            setDegreesListener((int) weatherData.getTempData().getTemp());
        }
    }

    private void setRefreshListener() {
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Object> weatherObservable = ((UserDetailsProvider) getActivity()).fetchNewWeatherData();
                Subscriber<Object> weatherSubscriber = new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        WeatherData newWeatherData = ((UserDetailsProvider) getActivity()).getCurrentWeatherData();
                        weatherData.copyWeatherData(newWeatherData);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateViews();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                };
                weatherObservable.subscribe(weatherSubscriber);
            }
        });
    }

    private void queryInputs() {
        ArrayList<ComfortLevelEntry> comfortLevelEntryArrayList = (ArrayList<ComfortLevelEntry>) currentUser.get(ComfortLevelUtil.KEY_TODAY_ENTRIES);
        adapter.addAll(comfortLevelEntryArrayList);
    }

    private void listenerSetup() {
        submitListener();
        swipeListener();
    }

    private void submitListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int comfortLevel = (int) slComfortLevel.getValue();
                slComfortLevel.setValue(DEFAULT_VALUE);
                int temp = (int) weatherData.getTempData().getTemp();
                ComfortLevelEntry newEntry = new ComfortLevelEntry(currentUser, temp, comfortLevel);
                newEntry.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        try {
                            ComfortLevelUtil.updateEntriesList(currentUser, newEntry);
                            entries.add(INSERT_INDEX, newEntry);
                            adapter.notifyItemInserted(INSERT_INDEX);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void swipeListener() {
        rvInputs.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                ComfortLevelEntry comfortEntry = entries.get(position);
                adapter.remove(position);

                LevelsTracker tracker;
                try {
                    tracker = comfortEntry.getLevelTracker();
                    tracker.removeEntry(comfortEntry);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                comfortEntry.deleteEntryFromTodayList(currentUser);
                comfortEntry.deleteEntry();
            }

            @Override
            public void onSwipedRight(int position) {
            }

        });
    }

    private void setDegreesListener(int temp) {
        DegreeSwitchListener degreeSwitchListener = new DegreeSwitchListener(tvFahrenheit, tvCelsius, getActivity());
        degreeSwitchListener.setDegreeListeners(new DegreeSwitchCallback() {
            @Override
            public void onDegreeSwitched() {
                UserPreferenceUtil.degreeConversion(getActivity(), tvCurrentTemp, temp);
                UserPreferenceUtil.updateIsFahrenheitLocally(getActivity(), ((UserDetailsProvider) getActivity()).getIsFahrenheit());
                adapter.notifyDataSetChanged();
            }
        });
    }
}
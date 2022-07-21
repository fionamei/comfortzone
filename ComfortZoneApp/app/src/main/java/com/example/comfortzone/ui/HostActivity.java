package com.example.comfortzone.ui;

import static com.example.comfortzone.utils.UserPreferenceUtil.KEY_IS_FAHRENHEIT;
import static com.example.comfortzone.utils.UserPreferenceUtil.KEY_SAVED_CITIES;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.comfortzone.R;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.flight.ui.FlightFragment;
import com.example.comfortzone.initial.LoginActivity;
import com.example.comfortzone.input.ui.InputFragment;
import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.WeatherData.Coordinates;
import com.example.comfortzone.profile.ui.ProfileFragment;
import com.example.comfortzone.utils.ComfortCalcUtil;
import com.example.comfortzone.utils.ComfortLevelUtil;
import com.example.comfortzone.utils.LocationUtil;
import com.example.comfortzone.utils.WeatherDbUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import rx.Observable;
import rx.Subscriber;

public class HostActivity extends AppCompatActivity implements UserDetailsProvider {

    public static final String TAG = "Main Activity";
    public static final int PERMISSION_ID = 44;

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private FlightFragment flightFragment;
    private InputFragment inputFragment;
    private ProfileFragment profileFragment;
    private boolean isLoading;
    private Coordinates coordinates;
    private String iataCode;
    private HashSet<Integer> savedCities;
    private ParseUser currentUser;
    private Fragment currentFragment;
    private Boolean[] isFahrenheit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLoading = true;

        maybeRequestPermissions();
        maybeUpdateComfortLevel();
        getObjects();
        initViews();
        createFragments();
        listenerSetup();
    }

    private void maybeRequestPermissions() {
        if (hasPermissions()) {
            loadData();
            return;
        }
        requestPermissions();
    }

    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ID);
    }

    private void maybeUpdateComfortLevel() {
        currentUser = ParseUser.getCurrentUser();
        ArrayList<ComfortLevelEntry> todayEntries = (ArrayList<ComfortLevelEntry>) currentUser.get(ComfortLevelUtil.KEY_TODAY_ENTRIES);
        if (!todayEntries.isEmpty() && todayEntries.get(0).getUpdatedAt() != null && !DateUtils.isToday(todayEntries.get(0).getUpdatedAt().getTime())) {
            ComfortLevelUtil.updateComfortLevel(currentUser);
            ComfortCalcUtil.calculateAverages(currentUser);
        }
    }

    private void getObjects() {
        savedCities = new HashSet<>();
        isFahrenheit = new Boolean[1];
        ArrayList<Integer> savedIds = (ArrayList<Integer>) currentUser.get(KEY_SAVED_CITIES);
        if (savedIds != null) {
            savedCities.addAll(savedIds);
        }
        isFahrenheit[0] = (boolean) currentUser.get(KEY_IS_FAHRENHEIT);
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void createFragments() {
        flightFragment = new FlightFragment();
        inputFragment = new InputFragment();
        profileFragment = new ProfileFragment();
    }

    private void listenerSetup() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Pair<Integer, Integer> animations;
            if (isLoading) {
                animations = new Pair<>(0,0);
                currentFragment = new HostLoadingFragment();
            } else {
                animations = bottomNavSelected(item);
            }
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            animations.first,
                            animations.second
                    )
                    .replace(R.id.flContainer, currentFragment)
                    .commit();
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
    }

    @NonNull
    private Pair<Integer, Integer> bottomNavSelected(MenuItem item) {
        Pair<Integer, Integer> animations;
        switch (item.getItemId()) {
            case R.id.action_flight:
                currentFragment = flightFragment;
                animations = setAnimationLeftToRight();
                break;
            case R.id.action_input:
                animations = currentFragment == flightFragment ? setAnimationRightToLeft() : setAnimationLeftToRight();
                currentFragment = inputFragment;
                break;
            case R.id.action_profile:
            default:
                currentFragment = profileFragment;
                animations = setAnimationRightToLeft();
                break;
        }
        return animations;
    }

    private Pair<Integer, Integer> setAnimationRightToLeft() {
        return new Pair<>(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private Pair<Integer, Integer> setAnimationLeftToRight() {
        return new Pair<>(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            onLogoutButton();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLogoutButton() {
        ParseUser.logOutInBackground();
        Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
        goLoginActivity();

    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ID: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied. You cannot use the app.", Toast.LENGTH_SHORT).show();
                    requestPermissions();
                }
                return;
            }
        }
    }

    @Override
    public Coordinates getLocation() {
        return coordinates;
    }

    @Override
    public String getIataCode() {
        return iataCode;
    }

    @Override
    public HashSet<Integer> getSavedCities() {
        return savedCities;
    }

    @Override
    public Boolean[] getIsFahrenheit() {
        return isFahrenheit;
    }

    private void loadData() {
        Observable<Object> citiesSetupObservable = WeatherDbUtil.maybeUpdateCitiesList(this);
        Observable<Object> locationSetupObservable = LocationUtil.getLocationObservable(this);
        Observable<Object> mergedObservable = Observable.merge(new ArrayList<>(Arrays.asList(locationSetupObservable, citiesSetupObservable)));
        Subscriber<Object> dataSetupSubscriber = new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                bottomNavSelected(bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId()));
                fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, currentFragment)
                        .commit();
                isLoading = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object coordIataPair) {
                Pair<Coordinates, String> pair = (Pair<Coordinates, String>) coordIataPair;
                coordinates = pair.first;
                iataCode = pair.second;
            }
        };
        mergedObservable.subscribe(dataSetupSubscriber);
    }
}
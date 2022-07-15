package com.example.comfortzone.ui;
import static com.example.comfortzone.utils.WeatherDbUtil.maybeUpdateCitiesList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.comfortzone.R;
import com.example.comfortzone.initial.LoginActivity;
import com.example.comfortzone.flight.ui.FlightFragment;
import com.example.comfortzone.input.ui.InputFragment;
import com.example.comfortzone.profile.ProfileFragment;
import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.utils.ComfortCalcUtil;
import com.example.comfortzone.utils.ComfortLevelUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

import java.util.ArrayList;

public class HostActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private FlightFragment flightFragment;
    private InputFragment inputFragment;
    private ProfileFragment profileFragment;
    private Fragment fragment;
    private int enterAnimation;
    private int exitAnimation;


    public static final String TAG = "Main Activity";
    public static final int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maybeRequestPermissions();
        maybeUpdateCitiesList(this);
        maybeUpdateComfortLevel();
        initViews();
        createFragments();
        listenerSetup();
    }

    private void maybeRequestPermissions() {
        if (hasPermissions()) {
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<ComfortLevelEntry> todayEntries = (ArrayList<ComfortLevelEntry>) currentUser.get(ComfortLevelUtil.KEY_TODAY_ENTRIES);
        if (!todayEntries.isEmpty() && todayEntries.get(0).getUpdatedAt() != null && !DateUtils.isToday(todayEntries.get(0).getUpdatedAt().getTime())) {
            ComfortLevelUtil.updateComfortLevel(currentUser);
            ComfortCalcUtil.calculateAverages(currentUser);
        }
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
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_flight:
                        fragment = flightFragment;
                        getAnimationLeftToRight();
                        break;
                    case R.id.action_input:
                        if (fragment == flightFragment) {
                            getAnimationRightToLeft();
                        } else {
                            getAnimationLeftToRight();
                        }
                        fragment = inputFragment;
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = profileFragment;
                        getAnimationRightToLeft();
                        break;
                }
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                enterAnimation,
                                exitAnimation
                        )
                        .replace(R.id.flContainer, fragment, "input")
                        .commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
    }

    private void getAnimationRightToLeft() {
        enterAnimation = R.anim.enter_from_right;
        exitAnimation = R.anim.exit_to_left;
    }

    private void getAnimationLeftToRight() {
        enterAnimation = R.anim.enter_from_left;
        exitAnimation = R.anim.exit_to_right;
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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied. You cannot use the app.", Toast.LENGTH_SHORT).show();
                    requestPermissions();
                }
                return;
            }
        }
    }
}
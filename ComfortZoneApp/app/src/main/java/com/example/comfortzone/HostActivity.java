package com.example.comfortzone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.comfortzone.fragments.FlightFragment;
import com.example.comfortzone.fragments.InputFragment;
import com.example.comfortzone.fragments.ProfileFragment;
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

    public static final String TAG = "Main Activity";
    public static final int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maybeRequestPermissions();
        maybeUpdateComfortLevel();
        
        initViews();
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

    private void listenerSetup() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_flight:
                        fragment = new FlightFragment();
                        break;
                    case R.id.action_input:
                        fragment = new InputFragment();
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
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
package com.example.comfortzone.flight.listeners;

import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_CITY_ID;
import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_IATA;

import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.example.comfortzone.flight.ui.CityDetailActivity;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.google.android.material.card.MaterialCardView;
import com.parse.ParseUser;

public class CityListGestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final int BORDER_WIDTH = 5;
    private Activity activity;
    private ImageView ivCityIcon;
    private MaterialCardView cvCityRoot;
    private String iata;

    public CityListGestureListener(Activity activity, ImageView ivCityIcon, MaterialCardView cvCityRoot, String iata) {
        this.activity = activity;
        this.ivCityIcon = ivCityIcon;
        this.cvCityRoot = cvCityRoot;
        this.iata = iata;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return super.onDown(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        goToDetailView();
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        int cityId = cvCityRoot.getId();
        if (UserPreferenceUtil.isCityAlreadySaved(cityId, activity)) {
            UserPreferenceUtil.deleteSavedCity(currentUser, cityId, activity);
            cvCityRoot.setStrokeWidth(0);
            Toast.makeText(activity, "Unsaved!", Toast.LENGTH_SHORT).show();
        } else {
            UserPreferenceUtil.saveCity(ParseUser.getCurrentUser(), cvCityRoot.getId(), activity);
            cvCityRoot.setStrokeWidth(BORDER_WIDTH);
            Toast.makeText(activity, "Saved!", Toast.LENGTH_SHORT).show();
        }
        return super.onDoubleTap(e);
    }

    private void goToDetailView() {
        Intent intent = new Intent(activity, CityDetailActivity.class);
        intent.putExtra(ARG_CITY_ID, cvCityRoot.getId());
        intent.putExtra(ARG_IATA, iata);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, ivCityIcon, ivCityIcon.getTransitionName());
        activity.startActivity(intent, options.toBundle());
    }

}

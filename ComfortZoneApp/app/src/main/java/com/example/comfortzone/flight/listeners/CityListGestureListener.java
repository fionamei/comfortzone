package com.example.comfortzone.flight.listeners;

import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_CITY_ID;
import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_IATA;

import android.app.Activity;
import android.content.Context;
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
    private Context context;
    private ImageView ivCityIcon;
    private MaterialCardView cvCityRoot;
    private String iata;

    public CityListGestureListener(Context context, ImageView ivCityIcon, MaterialCardView cvCityRoot, String iata) {
        this.context = context;
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
        if (UserPreferenceUtil.isCityAlreadySaved(currentUser, cityId)) {
            UserPreferenceUtil.deleteSavedCity(currentUser, cityId);
            cvCityRoot.setStrokeWidth(0);
            Toast.makeText(context, "Unsaved!", Toast.LENGTH_SHORT).show();
        } else {
            UserPreferenceUtil.saveCity(ParseUser.getCurrentUser(), cvCityRoot.getId());
            cvCityRoot.setStrokeWidth(BORDER_WIDTH);
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
        }
        return super.onDoubleTap(e);
    }

    private void goToDetailView() {
        Intent intent = new Intent(context, CityDetailActivity.class);
        intent.putExtra(ARG_CITY_ID, cvCityRoot.getId());
        intent.putExtra(ARG_IATA, iata);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, ivCityIcon, ivCityIcon.getTransitionName());
        context.startActivity(intent, options.toBundle());
    }

}

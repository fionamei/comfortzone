package com.example.comfortzone.flight.models;

import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_CITY_ID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import com.example.comfortzone.flight.ui.CityDetailActivity;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.parse.ParseUser;

public class CityListGestureListener extends GestureDetector.SimpleOnGestureListener {

    private Context context;
    private ImageView ivCityIcon;
    private CardView cvCityRoot;

    public CityListGestureListener(Context context, ImageView ivCityIcon, CardView cvCityRoot) {
        this.context = context;
        this.ivCityIcon = ivCityIcon;
        this.cvCityRoot = cvCityRoot;
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
        UserPreferenceUtil.saveCity(ParseUser.getCurrentUser(), cvCityRoot.getId());
        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
        return super.onDoubleTap(e);
    }

    private void goToDetailView() {
        Intent intent = new Intent(context, CityDetailActivity.class);
        intent.putExtra(ARG_CITY_ID, cvCityRoot.getId());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, ivCityIcon, ivCityIcon.getTransitionName());
        context.startActivity(intent, options.toBundle());
    }

}

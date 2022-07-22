package com.example.comfortzone.flight.ui;

import static com.example.comfortzone.flight.listeners.CityListGestureListener.BORDER_WIDTH;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comfortzone.R;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.flight.listeners.CityListGestureListener;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.ViewHolder> {

    private Activity activity;
    private List<WeatherData> cityList;
    private String iata;
    private Boolean[] isFahrenheit;

    public FlightsAdapter(Activity activity, List<WeatherData> cityList, String iata) {
        this.activity = activity;
        this.cityList = cityList;
        this.iata = iata;
    }

    @NonNull
    @Override
    public FlightsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        isFahrenheit = ((UserDetailsProvider) activity).getIsFahrenheit();
        WeatherData city = cityList.get(position);
        holder.bind(city, isFahrenheit);
        holder.cvCityRoot.setId(city.getId());
    }


    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void addAll(List<WeatherData> cities) {
        cityList.addAll(cities);
        notifyDataSetChanged();
    }

    public void updateCities(List<WeatherData> weathers) {
        cityList.clear();
        addAll(weathers);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTemperature;
        private TextView tvCityName;
        private MaterialCardView cvCityRoot;
        private ImageView ivCityIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
            listenerSetup();
        }

        private void initViews(View itemView) {
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            cvCityRoot = itemView.findViewById(R.id.cvCityRoot);
            ivCityIcon = itemView.findViewById(R.id.ivCityIcon);
        }

        public void bind(WeatherData city, Boolean[] isFahrenheit) {
            if (isFahrenheit[0]) {
                tvTemperature.setText(String.valueOf((int) city.getTempData().getTemp()));
            } else {
                tvTemperature.setText(String.valueOf(UserPreferenceUtil.convertFahrenheitToCelsius(city.getTempData().getTemp())));
            }
            tvCityName.setText(city.getCity());
            Glide.with(activity).load(city.getImage()).circleCrop().into(ivCityIcon);
            if (UserPreferenceUtil.isCityAlreadySaved(city.getId(), activity)) {
                cvCityRoot.setStrokeWidth(BORDER_WIDTH);
            } else {
                cvCityRoot.setStrokeWidth(0);
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private void listenerSetup() {
            cvCityRoot.setOnTouchListener(new View.OnTouchListener() {
                final GestureDetector gestureDetector = new GestureDetector(activity, new CityListGestureListener(activity, ivCityIcon, cvCityRoot, iata));
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }
    }
}

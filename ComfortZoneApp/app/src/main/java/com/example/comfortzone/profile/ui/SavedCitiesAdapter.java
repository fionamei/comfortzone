package com.example.comfortzone.profile.ui;

import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_CITY_ID;
import static com.example.comfortzone.flight.ui.CityDetailActivity.ARG_IATA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comfortzone.R;
import com.example.comfortzone.flight.ui.CityDetailActivity;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.parse.ParseUser;

import java.util.List;

public class SavedCitiesAdapter extends RecyclerView.Adapter<SavedCitiesAdapter.ViewHolder> {

    private Context context;
    private List<WeatherData> savedCities;
    private String iata;

    public SavedCitiesAdapter (Context context, List<WeatherData> savedCities, String iata) {
        this.context = context;
        this.savedCities = savedCities;
        this.iata = iata;
    }

    @NonNull
    @Override
    public SavedCitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedCitiesAdapter.ViewHolder holder, int position) {
        WeatherData city = savedCities.get(position);
        holder.bind(city);
        holder.cvRootView.setId(city.getId());
    }

    @Override
    public int getItemCount() {
        return savedCities.size();
    }

    public void addAll(List<WeatherData> cities) {
        savedCities.addAll(cities);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        int toDelete = savedCities.get(position).getId();
        UserPreferenceUtil.deleteSavedCity(ParseUser.getCurrentUser(), toDelete);
        savedCities.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCityIcon;
        private TextView tvCityName;
        private CardView cvRootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
            setSavedCityListener();
        }

        private void initViews(View itemView) {
            tvCityName = itemView.findViewById(R.id.tvCityName);
            ivCityIcon = itemView.findViewById(R.id.ivCityIcon);
            cvRootView = itemView.findViewById(R.id.cvRootView);
        }

        public void bind(WeatherData city) {
            tvCityName.setText(city.getCity());
            Glide.with(context).load(city.getImage()).circleCrop().into(ivCityIcon);
        }

        public void setSavedCityListener() {
            cvRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CityDetailActivity.class);
                    intent.putExtra(ARG_CITY_ID, v.getId());
                    intent.putExtra(ARG_IATA, iata);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, ivCityIcon, ivCityIcon.getTransitionName());
                    context.startActivity(intent, options.toBundle());
                }
            });
        }
    }
}

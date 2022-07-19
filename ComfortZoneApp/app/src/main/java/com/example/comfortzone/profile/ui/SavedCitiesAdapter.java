package com.example.comfortzone.profile.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comfortzone.R;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.parse.ParseUser;

import java.util.List;

public class SavedCitiesAdapter extends RecyclerView.Adapter<SavedCitiesAdapter.ViewHolder> {

    private Context context;
    private List<WeatherData> savedCities;

    public SavedCitiesAdapter (Context context, List<WeatherData> savedCities) {
        this.context = context;
        this.savedCities = savedCities;
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
    }

    @Override
    public int getItemCount() {
        return savedCities.size();
    }

    public void addAll(List<WeatherData> cities) {
        savedCities.addAll(cities);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return this.context;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            tvCityName = itemView.findViewById(R.id.tvCityName);
            ivCityIcon = itemView.findViewById(R.id.ivCityIcon);
        }

        public void bind(WeatherData city) {
            tvCityName.setText(city.getCity());
            Glide.with(context).load(city.getImage()).circleCrop().into(ivCityIcon);
        }
    }
}

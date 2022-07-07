package com.example.comfortzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.models.WeatherData;

import java.util.List;

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.ViewHolder> {

    private Context context;
    private List<WeatherData> cityList;

    public FlightsAdapter(Context context, List<WeatherData> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public FlightsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherData city = cityList.get(position);
        holder.bind(city);
    }


    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void addAll(List<WeatherData> cities) {
        cityList.addAll(cities);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvComfortLevel;
        private TextView tvTemperature;
        private TextView tvCityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            tvComfortLevel = itemView.findViewById(R.id.tvComfortLevel);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvCityName = itemView.findViewById(R.id.tvCityName);
        }

        public void bind(WeatherData city) {
            tvTemperature.setText(String.valueOf(city.getTempData().getTemp()));
            tvCityName.setText(city.getCity());
        }
    }
}

package com.example.comfortzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.models.WeatherData;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

    public void filterClearAndAdd(List<WeatherData> cities) {
        cityList.clear();
        addAll(cities);
    }

    public void sortIncTemp() {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getTempData().getTemp() < o2.getTempData().getTemp() ? -1 : 1;
            }
        });
        notifyDataSetChanged();
    }

    public void sortDecTemp() {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getTempData().getTemp() > o2.getTempData().getTemp() ? -1 : 1;
            }
        });
        notifyDataSetChanged();
    }

    public void sortIncRank() {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getRank() < o2.getRank() ? -1 : 1;
            }
        });
        notifyDataSetChanged();
    }

    public void sortDecRank() {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getRank() > o2.getRank() ? -1 : 1;
            }
        });
        notifyDataSetChanged();
    }

    public void sortIncDist() {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getDistanceBetween() > o2.getDistanceBetween() ? -1 : 1;
            }
        });
        notifyDataSetChanged();
    }

    public void sortDecDist() {
        cityList.sort(new Comparator<WeatherData>() {
            @Override
            public int compare(WeatherData o1, WeatherData o2) {
                if (o1 == o2)
                    return 0;
                return o1.getDistanceBetween() < o2.getDistanceBetween() ? -1 : 1;
            }
        });
        notifyDataSetChanged();
    }

    public void searchCity(CharSequence name, List<WeatherData> cities) {
        cityList = cities.stream().filter(city -> city.getCity().toLowerCase(Locale.ROOT).contains(name)).collect(Collectors.toList());
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

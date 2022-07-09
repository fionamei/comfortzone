package com.example.comfortzone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.comfortzone.fragments.FlightFragment;
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTemperature;
        private TextView tvCityName;
        private CardView cvCityRoot;
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

        public void bind(WeatherData city) {
            tvTemperature.setText(String.valueOf(city.getTempData().getTemp()));
            tvCityName.setText(city.getCity());
            Glide.with(context).load(city.getImage()).circleCrop().into(ivCityIcon);
            ivCityIcon.setTransitionName(context.getResources().getString(R.string.cityIcon));
        }

        private void listenerSetup() {
            cvCityRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CityDetailActivity.class);
                    intent.putExtra(context.getResources().getString(R.string.cityId), v.getId());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, ivCityIcon, context.getResources().getString(R.string.cityIcon));
                    context.startActivity(intent, options.toBundle());
                }
            });
        }
    }
}

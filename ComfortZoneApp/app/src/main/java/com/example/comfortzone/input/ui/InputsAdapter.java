package com.example.comfortzone.input.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.R;
import com.example.comfortzone.callback.UserDetailsProvider;
import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.utils.UserPreferenceUtil;
import com.parse.ParseException;

import java.text.DateFormat;
import java.util.Collections;
import java.util.List;

public class InputsAdapter extends RecyclerView.Adapter<InputsAdapter.ViewHolder> {

    private Activity activity;
    private List<ComfortLevelEntry> entries;
    private Boolean[] isFahrenheit;

    public InputsAdapter(Activity activity, List<ComfortLevelEntry> entries) {
        this.activity = activity;
        this.entries = entries;
    }

    @NonNull
    @Override
    public InputsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_input, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputsAdapter.ViewHolder holder, int position) {
        isFahrenheit = ((UserDetailsProvider) activity).getIsFahrenheit();
        ComfortLevelEntry entry = entries.get(position);
        try {
            holder.bind(entry, isFahrenheit);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void addAll(List<ComfortLevelEntry> inputList) {
        entries.addAll(inputList);
        Collections.reverse(entries);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        entries.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTemp;
        private TextView tvComfortLevel;
        private TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            tvTemp = itemView.findViewById(R.id.tvTemp);
            tvComfortLevel = itemView.findViewById(R.id.tvComfortLevel);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(ComfortLevelEntry entry, Boolean[] isFahrenheit) throws ParseException {
            if (isFahrenheit[0]) {
                tvTemp.setText(String.valueOf(entry.getTemp()));
            } else {
                tvTemp.setText(String.valueOf(UserPreferenceUtil.convertFahrenheitToCelsius(entry.getTemp())));
            }
            tvComfortLevel.setText(String.valueOf(entry.getComfortLevel()));
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(entry.getCreatedAt());
            tvTime.setText(time);
        }
    }
}

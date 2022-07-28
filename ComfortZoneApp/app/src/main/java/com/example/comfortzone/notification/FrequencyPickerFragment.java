package com.example.comfortzone.notification;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.comfortzone.R;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.TimeUnit;

public class FrequencyPickerFragment extends DialogFragment implements NumberPicker.OnValueChangeListener {

    public static final long MINUTE_LONG = TimeUnit.MINUTES.toMillis(1L);
    public static final long HOUR_LONG = TimeUnit.HOURS.toMillis(1L);
    public static final long DAY_LONG = TimeUnit.DAYS.toMillis(1L);
    private static final int MIN = 0;
    private static final int HOUR = 1;
    private static final int MAX_MINS = 60;
    private NumberPicker npVal;
    private NumberPicker npUnit;

    public FrequencyPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frequency_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = createPickerView(layoutInflater);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getContext().getString(R.string.frequency_description))
                .setPositiveButton(getContext().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int val = npVal.getValue();
                        int unitIndex = npUnit.getValue();
                        NotificationUtil.saveFrequency(getActivity(), calculateFrequencyLong(val, unitIndex));
                        changeButtonText();
                        NotificationUtil.startNotification(getActivity());
                    }
                })
                .setView(view);
        return builder.create();
    }

    @NonNull
    private View createPickerView(LayoutInflater li) {
        View view = li.inflate(R.layout.fragment_frequency_picker, null);
        initViews(view);
        populateViews();
        return view;
    }

    private void initViews(View view) {
        npVal = view.findViewById(R.id.npVal);
        npUnit = view.findViewById(R.id.npUnit);
    }

    private void populateViews() {
        String[] units = getActivity().getResources().getStringArray(R.array.frequencyUnits);
        npUnit.setDisplayedValues(units);
        npVal.setMinValue(1);
        npVal.setMaxValue(MAX_MINS);
        npUnit.setMinValue(0);
        npUnit.setMaxValue(units.length - 1);
    }

    private long calculateFrequencyLong(int value, int unitIndex) {
        long unitMultiplier = getUnitMultiplier(unitIndex);
        return value * unitMultiplier;
    }

    private long getUnitMultiplier(int unitIndex) {
        if (unitIndex == MIN) {
            return MINUTE_LONG;
        } else if (unitIndex == HOUR) {
            return HOUR_LONG;
        } else {
            return DAY_LONG;
        }
    }

    private void changeButtonText() {
        MaterialButton btnFrequency = ((NotificationActivity) getActivity()).findViewById(R.id.btnFrequency);
        btnFrequency.setText(NotificationUtil.getSavedFrequencyTime(getContext()));
    }

}
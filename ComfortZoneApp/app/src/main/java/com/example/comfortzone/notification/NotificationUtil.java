package com.example.comfortzone.notification;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.comfortzone.notification.FrequencyPickerFragment.DAY_LONG;
import static com.example.comfortzone.notification.FrequencyPickerFragment.HOUR_LONG;
import static com.example.comfortzone.notification.FrequencyPickerFragment.MINUTE_LONG;
import static com.example.comfortzone.notification.NotificationActivity.KEY_IS_NOTIFICATION;
import static com.example.comfortzone.ui.HostActivity.REQUEST_CODE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Pair;

import com.example.comfortzone.R;
import com.example.comfortzone.ui.HostActivity;

import java.util.Calendar;
import java.util.Date;

public class NotificationUtil {

    public static final String ARG_AUTO_OPEN_SCREEN = "FRAGMENT";
    public static final String AUTO_OPEN_PROFILE = "profile";
    public static final String AUTO_OPEN_INPUT = "input";
    public static final String AUTO_OPEN_FLIGHT = "flight";
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String AM = " AM";
    public static final String PM = " PM";
    public static final String KEY_NOTIF_HOUR = "notificationHour";
    public static final String KEY_NOTIF_MIN = "notificationMinute";
    public static final String KEY_FREQUENCY = "notificationFrequency";
    public static final int MID_DAY = 12;
    public static final int DOUBLE_DIGITS = 10;
    public static final int DEFAULT_HOUR = 10;
    public static final int DEFAULT_MIN = 0;

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }

    private static PendingIntent getBroadcastPendingIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        return PendingIntent.getBroadcast(context.getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public static void startNotification(Context context) {
        Calendar calendar = getAlarmTime(context);
        PendingIntent pendingIntent = getBroadcastPendingIntent(context);
        AlarmManager alarmManager = getAlarmManager(context);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), getSavedFrequency(context), pendingIntent);
        }
    }

    @NonNull
    private static Calendar getAlarmTime(Context context) {
        Pair<Integer, Integer> savedTime = NotificationUtil.getNotificationTime(context);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, savedTime.first);
        calendar.set(Calendar.MINUTE, savedTime.second);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static void cancelNotification(Context context) {
        PendingIntent pendingIntent = getBroadcastPendingIntent(context);
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(pendingIntent);
    }

    public static void createNotification(Context context) {

        Intent intent = new Intent(context, HostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ARG_AUTO_OPEN_SCREEN, AUTO_OPEN_INPUT);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.logo);
        notificationBuilder.setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.notification_content))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                context.getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
        if (notificationManager != null) {
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(REQUEST_CODE, notificationBuilder.build());
        }
    }

    public static void updateNotificationLocally(Activity activity, boolean isChecked) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_IS_NOTIFICATION, isChecked);
        editor.apply();
    }

    public static boolean isNotificationEnabled(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_IS_NOTIFICATION, true);
    }

    @NonNull
    public static StringBuilder formatPickedTime(int pickedHour, int pickedMinute) {
        StringBuilder timePicked = new StringBuilder();
        if (pickedHour > MID_DAY) {
            if (pickedMinute < DOUBLE_DIGITS) {
                timePicked.append(pickedHour - MID_DAY).append(":0").append(pickedMinute).append(PM);
            } else {
                timePicked.append(pickedHour - MID_DAY).append(":").append(pickedMinute).append(PM);
            }
        } else if (pickedHour == MID_DAY) {
            if (pickedMinute < DOUBLE_DIGITS) {
                timePicked.append(pickedHour).append(":0").append(pickedMinute).append(PM);
            } else {
                timePicked.append(pickedHour).append(":").append(pickedMinute).append(PM);
            }
        } else {
            if (pickedMinute < DOUBLE_DIGITS) {
                timePicked.append(pickedHour).append(":0").append(pickedMinute).append(AM);
            } else {
                timePicked.append(pickedHour).append(":").append(pickedMinute).append(AM);
            }
        }
        return timePicked;
    }

    public static void saveNotificationTime(Activity activity, int hour, int minute) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_NOTIF_HOUR, hour);
        editor.putInt(KEY_NOTIF_MIN, minute);
        editor.apply();
    }

    public static Pair<Integer, Integer> getNotificationTime(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
        int hour = sharedPref.getInt(KEY_NOTIF_HOUR, DEFAULT_HOUR);
        int min = sharedPref.getInt(KEY_NOTIF_MIN, DEFAULT_MIN);
        return new Pair<>(hour, min);
    }

    public static void saveFrequency(Activity activity, long frequency) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(KEY_FREQUENCY, frequency);
        editor.apply();
    }

    public static long getSavedFrequency(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.key_shared_pref_activity), MODE_PRIVATE);
        return sharedPref.getLong(KEY_FREQUENCY, DAY_LONG);
    }

    public static String getSavedFrequencyTime(Context context) {
        long time = getSavedFrequency(context);
        if (time / DAY_LONG > 0) {
            return String.format("%s Days", time / DAY_LONG);
        } else if (time / HOUR_LONG > 0) {
            return String.format("%s Hours", time / HOUR_LONG);
        } else {
            return String.format("%s Minutes", time / MINUTE_LONG);
        }
    }
}

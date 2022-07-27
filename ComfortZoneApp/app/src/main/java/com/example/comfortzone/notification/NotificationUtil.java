package com.example.comfortzone.notification;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
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

import com.example.comfortzone.R;
import com.example.comfortzone.ui.HostActivity;

import java.util.Calendar;
import java.util.Date;

public class NotificationUtil {

    public static final int NOTIF_TIME = 10;
    public static final String ARG_AUTO_OPEN_SCREEN = "FRAGMENT";
    public static final String AUTO_OPEN_PROFILE = "profile";
    public static final String AUTO_OPEN_INPUT = "input";
    public static final String AUTO_OPEN_FLIGHT = "flight";
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String AM = " AM";
    public static final String PM = " PM";
    public static final int MID_DAY = 12;
    public static final int DOUBLE_DIGITS = 10;

    public static void notificationSetup(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, NOTIF_TIME);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public static void cancelNotification(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
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
}